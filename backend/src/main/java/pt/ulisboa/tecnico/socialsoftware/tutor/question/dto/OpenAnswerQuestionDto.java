package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED;


public class OpenAnswerQuestionDto extends QuestionDetailsDto {
    //if there is a question with the same title, if so throw an exception
    //if there is a question with the same content, if so throw an exception
    //else create an OpenAnswerQuestion

    private String answer;
    private boolean regex = true;

    public OpenAnswerQuestionDto(){    }

    public OpenAnswerQuestionDto(OpenAnswerQuestion question) {
        setAnswer(question.getAnswer());
        setRegex(question.isRegex());
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) throws TutorException {
        this.answer= answer;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        if (!regex) {
            this.regex = false;
        }
    }

    @Override
    public String toString() {

        if (regex) {
            return "OpenAnswerQuestionDto{" +
                    "answer'" + answer +
                    ", with regex activated} ";
        } else {
            return "OpenAnswerQuestionDto{" +
                    "answer'" + answer +
                    ", with regex deactivated} ";
        }
    }

    @Override
    public void update(OpenAnswerQuestion question) {
        question.update(this);
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenAnswerQuestion(question, this);
    }


}
