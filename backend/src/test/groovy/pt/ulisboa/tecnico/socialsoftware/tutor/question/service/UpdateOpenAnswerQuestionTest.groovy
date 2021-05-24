package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class UpdateOpenAnswerQuestionTest extends SpockTest {
    def question
    def openAnswerQuestion
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create an open answer question"
        openAnswerQuestion = new Question()
        openAnswerQuestion.setKey(2)
        openAnswerQuestion.setTitle(QUESTION_1_TITLE)
        openAnswerQuestion.setContent(QUESTION_1_CONTENT)
        openAnswerQuestion.setStatus(Question.Status.AVAILABLE)
        openAnswerQuestion.setNumberOfAnswers(1)
        openAnswerQuestion.setNumberOfCorrect(1)
        openAnswerQuestion.setCourse(externalCourse)
        openAnswerQuestion.setImage(image)
        def openAnswerQuestionDetails = new OpenAnswerQuestion()
        openAnswerQuestionDetails.setAnswer(ANSWER_1_CONTENT)
        openAnswerQuestionDetails.setRegex(false)

        openAnswerQuestion.setQuestionDetails(openAnswerQuestionDetails)

        questionDetailsRepository.save(openAnswerQuestionDetails)
        questionRepository.save(openAnswerQuestion)
    }

    def "update an open answer question"() {
        given: "an open answer question"
        def questionDto= new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)

        def openAnswerQuestionDetailsDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDetailsDto.setAnswer(ANSWER_2_CONTENT)
        openAnswerQuestionDetailsDto.setRegex(true)
        questionDto.setQuestionDetailsDto(openAnswerQuestionDetailsDto)

        when:
        questionService.updateQuestion(openAnswerQuestion.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == openAnswerQuestion.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 1
        result.getNumberOfCorrect() == 1
        result.getImage() != null
        and: "the answer is not changed"
        def repoOpen = (OpenAnswerQuestion) result.getQuestionDetails()
        repoOpen.getAnswer() == ANSWER_1_CONTENT
        !repoOpen.isRegex()
    }

    def "update an answer and regex option in an open answer question"() {
        given: "an open answer question"
        def questionDto= new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)

        def openAnswerQuestionDetailsDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDetailsDto.setAnswer(ANSWER_2_CONTENT)
        openAnswerQuestionDetailsDto.setRegex(true)
        questionDto.setQuestionDetailsDto(openAnswerQuestionDetailsDto)

        when:
        questionService.updateQuestion(openAnswerQuestion.getId(),questionDto)

        then: 'are not changed'
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == openAnswerQuestion.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 1
        result.getNumberOfCorrect() == 1
        result.getImage() != null
        and: "the answer is changed"
        def repoOpen = (OpenAnswerQuestion) result.getQuestionDetails()
        repoOpen.getAnswer() == ANSWER_2_CONTENT
        repoOpen.isRegex()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
