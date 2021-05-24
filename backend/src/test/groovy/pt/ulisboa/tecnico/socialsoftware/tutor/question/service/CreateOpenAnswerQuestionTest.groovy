package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import spock.lang.Unroll

@DataJpaTest
class CreateOpenAnswerQuestionTest extends SpockTest {

    def "create an open answer question"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer(ANSWER_1_CONTENT)

        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoOpen = (OpenAnswerQuestion) repoResult.getQuestionDetails()
        repoOpen.getAnswer() == ANSWER_1_CONTENT
        repoOpen.isRegex()

    }

    def "cannot create an open answer question without an answer" (){
        //every OQ need to have an answer
        //an exception is thrown

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer()

        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED

    }

    def "create an open answer question with regex deactivated" (){
        //with regex activated the question will be automatically correct

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer(ANSWER_1_CONTENT)
        openAnswerQuestionDto.setRegex(false)

        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoOpen = (OpenAnswerQuestion) repoResult.getQuestionDetails()
        repoOpen.getAnswer() == ANSWER_1_CONTENT
        !repoOpen.isRegex()

    }

    def "create an open answer question with an image" (){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer(ANSWER_1_CONTENT)

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)
        repoResult.getImage().getId() != null
        repoResult.getImage().getUrl() == IMAGE_1_URL
        repoResult.getImage().getWidth() == 20

        def repoOpen = (OpenAnswerQuestion) repoResult.getQuestionDetails()
        repoOpen.getAnswer() == ANSWER_1_CONTENT
        repoOpen.isRegex()

    }

    @Unroll
    def "fail to create any question for invalid/non-existent course (#nonExistentId)"(Integer nonExistentId) {
        given: "any multiple choice question dto"
        def questionDto = new QuestionDto()
        when:
        questionService.createQuestion(nonExistentId, questionDto)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_NOT_FOUND
        where:
        nonExistentId << [-1, 0, 200]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
