package Napredno.K1.quiz;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

abstract class Question implements Comparable<Question>{
    protected String text;
    int points;

    public Question(String text, int points) {
        this.text = text;
        this.points = points;
    }
    abstract String type();
    abstract double answer(String ans);

    @Override
    public int compareTo(Question o) {
        return Double.compare(this.points,o.points);
    }
}
class MultipleChoiceQuestion extends Question{
    String answer;
    public MultipleChoiceQuestion(String text, int points,String answer) {
        super(text, points);
        this.answer=answer;
    }

    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s",text,points,answer);
    }

    @Override
    String type() {
        return "MC";
    }

    @Override
    double answer(String ans) {
        if(ans.equals(answer)) return points*1.0;

        return points * 0.20 * (-1);
    }
}
class TrueFalseQuestion extends Question{
    boolean answer;

    public TrueFalseQuestion(String text, int points,boolean answer) {
        super(text, points);
        this.answer=answer;
    }

    @Override
    public String toString() {
        String ans="";
        if(answer)ans="true";
        else ans="false";
        return String.format("True/False Question: %s Points: %d Answer: %s",text,points,ans);
    }


    @Override
    String type() {
        return "TF";
    }

    @Override
    public double answer(String ans) {
        if(Boolean.parseBoolean(ans)==answer){
            return points;
        }
        return 0.0;
    }
}

class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}
class Quiz{
    List<Question> questions;
    public Quiz(){
        questions=new ArrayList<>();
    }
    public void addQuestion(String questionData)throws InvalidOperationException{
        String []parts=questionData.split(";");
        if(parts[0].equals("MC")){
            char ans='A';
            int i;
            for (i = 0; i < 5; i++) {
                if(parts[3].charAt(0)==ans) {
                    break;
                }
                ans++;
            }
            if(i==5)throw new InvalidOperationException(String.format("%s is not allowed option for this question",parts[3]));
            questions.add(new MultipleChoiceQuestion(parts[1],Integer.parseInt(parts[2]),parts[3]));
        }
        else{
            boolean type;
            type= parts[3].equals("true");
            questions.add(new TrueFalseQuestion(parts[1],Integer.parseInt(parts[2]),type));
        }
    }
    public void printQuiz(OutputStream os){
        PrintWriter pw=new PrintWriter(os);
        questions.stream().sorted(Comparator.reverseOrder()).forEach(pw::println);
        pw.flush();
        pw.close();
    }
    public void answerQuiz (List<String> answers, OutputStream os) throws InvalidOperationException{
        PrintWriter pw=new PrintWriter(os);
        double totalPoints=0.0;
        if(answers.size()!=questions.size())throw new InvalidOperationException("Answers and questions must be of same length!");
        for (int i=0; i < answers.size(); i++) {
            double point= questions.get(i).answer(answers.get(i));
            totalPoints+=point;
            pw.println(String.format("%d. %.2f",i+1,point));
        }
        pw.println(String.format("Total points: %.2f",totalPoints));

        pw.flush();
        pw.close();
    }

}
public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}

