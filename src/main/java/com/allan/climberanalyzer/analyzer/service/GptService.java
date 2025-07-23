package com.allan.climberanalyzer.analyzer.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.parser.HttpHeaderParser;
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

    private static final Logger gptUsageLogger = LoggerFactory.getLogger("GPT_USAGE");

    @Autowired
    private ExercisesRepo exercisesRepo;

    @Autowired
    private ExerciseService exerciseService;

    private final RestTemplate restTemplate;

    public GptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ExerciseDisplayDTO> generateResponse(GptRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        Map<String, String> systemPrompt = new HashMap<>();
        systemPrompt.put("role", "system");
        systemPrompt.put("content",
                """
                        You are a climbing training assistant. When a user asks about exercises, extract relevant keywords from their request and respond with ONLY a PostgreSQL array string format that matches their needs.
                        Available keywords to choose from:
                        Grip & Finger Strength: crimp_strength, finger_strength, hangboard_max_hangs, pinch_strength, pinch_endurance, system_board_pinching, sloper_endurance, open_hand_conditioning, sloper_hangs, forearm_endurance, sloper_pulling_strength, sloper_lock_off, open_hand_power, pocket_strength, isolated_finger_strength, hangboard_pockets, wide_pinch_strength, thumb_strength, pinch_block_training, undercling_strength, bicep_pulling, supinated_curls, undercling_antagonist, wrist_flexion_strength, undercling_support, grip_support_strength
                        Power & Dynamic Movement: dyno_power, leg_power, plyometrics, explosive_jumping, limit_bouldering, project_power, max_recruitment, high_intensity_bouldering, sloper_dynamics, dynamic_contact_strength, wrist_accuracy, dyno_accuracy, directional_jumping, static_dyno_generation, dyno_momentum, swing_timing, paddle_dyno, rhythmic_movement, multi_dyno, parkour_coordination, momentum_redirection, complex_dyno
                        Technique, Balance & Control: core_tension, static_body_control, precise_footwork, cutting_prevention, sloper_technique, sloper_precision, static_positioning, hover_control, pocket_technique, pocket_accuracy, finger_proprioception, slab_balance, slab_footwork, no_hands_climbing, center_of_gravity_control, slab_technique, bad_foothold_training, foot_trust, smearing_technique
                        Pressing, Mantles & Transitions: chest_pressing_strength, adduction_strength, mantle_antagonist, pressing_antagonist, shoulder_pressing, gaston_strength, vertical_press, gaston_antagonist, tricep_strength, mantle_pressing, pushing_strength, transition_strength, pull_to_press_power, muscle_up, mantle_transition
                        Mobility, Stability & Health: core_stability, anti_rotation, rotational_control, shoulder_health, shoulder_stability, rear_deltoids, injury_prevention, hip_mobility, high_step_mobility, rock_over_flexibility, deep_squat
                        Mental & Commitment: dyno_commitment, mental_training_dyno, fear_of_falling, movement_confidence
                        Examples:
                        User: 'I want to work on finger strength' → {crimp_strength,finger_strength,hangboard_max_hangs}
                        User: 'Help me with dynamic movements' → {dyno_power,explosive_jumping,paddle_dyno,dyno_momentum}
                        User: 'I struggle with slab climbing' → {slab_balance,slab_footwork,slab_technique,foot_trust}
                        User: 'My legs keep cutting on overhangs' → {core_tension,static_body_control,cutting_prevention}
                        User: 'I can't stick slopers' → {sloper_endurance,sloper_technique,sloper_dynamics,open_hand_conditioning}
                        User: 'I struggle with pinch grips' → {pinch_strength,pinch_endurance,wide_pinch_strength,system_board_pinching}
                        User: 'I'm afraid to commit to dynos' → {dyno_commitment,mental_training_dyno,fear_of_falling}
                        User: 'How can I get better at mantles?' → {mantle_pressing,tricep_strength,transition_strength,mantle_transition}
                        User: 'I need better footwork on tiny holds' → {precise_footwork,foot_trust,bad_foothold_training,smearing_technique}
                        Respond with only the PostgreSQL array format (curly braces, comma-separated, no quotes around individual items), no other text. """

        );
        requestBody.put("messages",
                Arrays.asList(systemPrompt, Map.of("role", "user", "content", request.getMessage())));

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
