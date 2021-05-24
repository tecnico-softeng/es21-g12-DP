package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();
    @JsonProperty("ordered")
    private boolean ordered = false;
     @JsonProperty("numberOfOrdered")
    private Integer numberOfOrdered = 0;

    public Integer getNumberOfOrdered() {
        return numberOfOrdered;
    }

    public void setNumberOfOrdered(int n) {
        numberOfOrdered = n;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public MultipleChoiceQuestionDto() {
    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.ordered = question.isOrdered();
        this.numberOfOrdered = question.getNumberOfOrdered();
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new MultipleChoiceQuestion(question, this);
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}
