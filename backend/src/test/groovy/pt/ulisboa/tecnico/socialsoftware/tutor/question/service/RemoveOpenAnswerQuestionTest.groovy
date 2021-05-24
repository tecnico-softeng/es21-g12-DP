package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class RemoveOpenAnswerQuestionTest extends SpockTest {
    def openAnswerQuestion
    def teacher

    def setup() {
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        openAnswerQuestion = new Question()
        openAnswerQuestion.setKey(1)
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
        questionDetailsRepository.save(openAnswerQuestionDetails)
        questionRepository.save(openAnswerQuestion)
    }

    def "remove a open answer question"() {
        when:
        questionService.removeQuestion(openAnswerQuestion.getId())

        then: "the question is removeQuestion"
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        optionRepository.count() == 0L
    }

    def "remove a open answer question used in a quiz"() {
        given: "a open answer question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(openAnswerQuestion)
        quizQuestionRepository.save(quizQuestion)

        when:
        questionService.removeQuestion(openAnswerQuestion.getId())

        then: "the open answer question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_IS_USED_IN_QUIZ
    }

    def "remove a open answer question that has topics"() {
        given: 'a open answer question with topics'
        def topicDto = new TopicDto()
        topicDto.setName("name1")
        def topicOne = new Topic(externalCourse, topicDto)
        topicDto.setName("name2")
        def topicTwo = new Topic(externalCourse, topicDto)
        openAnswerQuestion.getTopics().add(topicOne)
        topicOne.getQuestions().add(openAnswerQuestion)
        openAnswerQuestion.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(openAnswerQuestion)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        when:
        questionService.removeQuestion(openAnswerQuestion.getId())

        then:
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        optionRepository.count() == 0L
        topicRepository.count() == 2L
        topicOne.getQuestions().size() == 0
        topicTwo.getQuestions().size() == 0
    }

    def "remove a open answer question that was submitted"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)

        and: "a questionSubmission"
        def questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(openAnswerQuestion)
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmissionRepository.save(questionSubmission)

        when:
        questionService.removeQuestion(openAnswerQuestion.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_DELETE_SUBMITTED_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
