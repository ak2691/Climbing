package com.allan.climberanalyzer.analyzer.DTOClass;

public class QuestionResults {
    // if score 0 then it is not a weakness, anything else >= 1 relates to an answer
    // choice
    // Ways to do this -> assign individual scores and relate each one to a specific
    // exercise.
    // then have an overall score that is looked at relative to the number of
    // weaknesses chosen
    /*
     * Lower score might mean a lack of technique, higher score might mean a lack of
     * general strength in those hold types
     * so we can assign lower scores to technical issues when it comes to the
     * questions and assign higher scores to strength issues
     * 
     * For something like a sloper: A score of 5 might be related to the answer
     * choice "My wrist feels unstable and weak when pulling on slopers"
     * A score of 1 might be something like
     * "My hands just slide off as soon as I grab the sloper or move a tiny bit"
     * 
     * 
     * If let's say, the user chose 6 hold types they feel weak in, and get a score
     * of 6, we output technical drills/advice for
     * each hold type. And then for the general advice just give an idea of what
     * type of climber they are
     * (technical, powerful, strong/static) Low overall score means that they lack
     * technique or understanding of body positioning, high overall score means a
     * lack of strength
     * Outside of specific exercises for the hold types, if they have a high score
     * (lack of strength) provide general strength exercises like weighted pull ups,
     * rows etc.
     * 
     * 
     * 
     * Another way to deal with the overall score is just to see trends. Since the
     * answer can be technical, lack of power/contact strength, or lock off/pure
     * static strength, we might need to look at each question
     * instead of just calculating the overall score. How many answers were a lack
     * of power/contact strength answer, how many were the inability to move from
     * the position (static/pure strength), how many were the ability to move due to
     * positioning?
     * Let's say a user chooses 6 holds/styles they feel weak in, and about 4 of
     * those were lack of power. Asides from the specific hold training, add in
     * general exercises like campusing or latching to improve shoulder power
     * stability and lat power.
     */
    private int crimpScore;
}
