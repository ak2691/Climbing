package com.allan.climberanalyzer.analyzer.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.parser.HttpHeaderParser;
import org.hibernate.boot.model.relational.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.GptRequest;
import com.allan.climberanalyzer.analyzer.DTOClass.GptResponse;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;

@Service
public class GptService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Autowired
    private DatabaseRateLimiter rateLimiter;

    private static final Logger gptUsageLogger = LoggerFactory.getLogger("GPT_USAGE");

    @Autowired
    private ExercisesRepo exercisesRepo;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private JwtService jwtService;

    private final RestTemplate restTemplate;

    public GptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ExerciseDisplayDTO> generateResponse(GptRequest request) throws BadRequestException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Long userId = jwtService.getUserIdFromToken(request.getJwtToken());
        if (!rateLimiter.isAllowed(userId, "/api/chat", 10, 60)) {
            throw new BadRequestException("You have reached your hourly limit of 10 requests, please try again later.");
        }
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        Map<String, String> systemPrompt = new HashMap<>();
        systemPrompt.put("role", "system");
        systemPrompt.put("content",
                """
                        You are a climbing training assistant. When a user asks about exercises, extract relevant keywords from their request and respond with ONLY a PostgreSQL array string format that matches their needs.

                        Available keywords to choose from:
                        Grip & Finger Strength: finger, hangboard, crimp, grip, maximum, strength, isometric, forearm, tendon, hang, weight, repeater, endurance, stamina, repetition, interval, campus, explosive, power, contact, dynamic, rung, plyometric, upper, board, climbing, spray, wall, system, movement, specific, functional, pinch, adduction, wide, squeeze, compression

                        Pulling & Upper Body: pull, pullup, weighted, vertical, lat, back, bicep, lockoff, onearm, unilateral, static, hold, position, stability, reach, burst, speed, recruitment, row, horizontal, rhomboid, posterior, chain

                        Pressing & Antagonist: shoulder, press, overhead, deltoid, gaston, mantle, push, facepull, rotator, cuff, prevention, health, dip, tricep, chest, antagonist, bodyweight, fly, bench, pushup

                        Core & Body Tension: core, abs, legrase, hanging, hipflexor, tension, overhang, feet, control, deadlift, glute, hamstring, lower, foothold

                        Footwork & Precision: calf, raise, leg, footwork, precision, toe, standing

                        Dynamic & Coordination: dyno, jump, coordination, commitment, custom, straight, traditional, pure, side, lateral, sideways, swing, arc, momentum, direction, steep, catch, angle, hop, singleleg, balance, landing, shuffle, barndoor, technical, sequence, running, crossleg, perpendicular, volume, traverse, lache, release, handtohand, advanced, nofoot, aerial, paddle, redirect, complex, timing

                        Examples:
                        User: 'I want to work on finger strength' → {finger,hangboard,crimp,grip,strength}
                        User: 'Help me with dynamic movements' → {dyno,explosive,power,dynamic,coordination}
                        User: 'I need better core tension' → {core,tension,hanging,control,stability}
                        User: 'My pulling strength is weak' → {pull,pullup,weighted,strength,upper}
                        User: 'I struggle with footwork' → {footwork,precision,calf,balance,technical}
                        User: 'I can't stick dynos' → {dyno,catch,commitment,explosive,momentum}
                        User: 'Need antagonist training' → {antagonist,chest,tricep,push,press}
                        User: 'Help with campus board training' → {campus,explosive,power,contact,rung}
                        User: 'I want to work on mantles' → {mantle,press,tricep,chest,push}

                        Respond with only the PostgreSQL array format (curly braces, comma-separated, no quotes around individual items), no other text. """

        );
        requestBody.put("messages",
                Arrays.asList(systemPrompt, Map.of("role", "user", "content", request.getMessage())));
        if (request.getMessage().trim().length() > 100) {
            throw new BadRequestException("Input too long, limit to 100 tokens or approximately 400 characters");
        }
        requestBody.put("max_completion_tokens", request.getMaxTokens());
        requestBody.put("temperature", request.getTemperature());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            GptResponse gptResponse = parseGptResponse(response.getBody(), request.getModel());
            List<ExerciseModel> exercises = exercisesRepo.findExercisesByKeywords(gptResponse.getResponse());
            List<ExerciseDisplayDTO> exerciseDisplay = exerciseService.convertExerciseDTO(exercises);
            gptUsageLogger.info("Model: {} | Tokens: {} | Keywords: {}", gptResponse.getModel(),
                    gptResponse.getTokensUsed(), gptResponse.getResponse());
            return exerciseDisplay;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get response from GPT API", e);
        }

    }

    private GptResponse parseGptResponse(Map<String, Object> responseBody, String model) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
            int tokensUsed = usage != null ? (Integer) usage.get("total_tokens") : 0;
            return new GptResponse(content, model, tokensUsed);
        }
        throw new RuntimeException("Invalid response format from GPT API");
    }

}
/*
 * Available keywords to choose from:
 * - Hold types: crimps, slopers, pinches, pockets, underclings, gastons
 * - Movement types: dynos, mantling, rock-over, press, coordination, balance,
 * traversing
 * - Training focus: strength, power, endurance, flexibility, mobility,
 * precision, accuracy, body-tension, core, commitment, mental
 * - Body parts: fingers, forearms, wrist, shoulders, chest, triceps, biceps,
 * back, legs, hips
 * - Wall angles: slab, vertical, overhanging, cave
 * - Equipment: hangboard, system-board, spray-wall, plyometric-box,
 * resistance-band, dumbbells
 * - Training methods: limit-bouldering, max-hangs, conditioning, drills,
 * progressions
 */
