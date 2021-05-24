package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import groovy.json.JsonOutput

import javax.validation.Valid

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateMultipleChoiceQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def course2
    def courseExecution
    def courseExecution2
    def response
    def teacher
    def student
    def questionDto
    def mcqDto

    def setup() {
        given: "a rest client"
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        course2 = new Course(COURSE_2_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course2)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
        courseExecution2 = new CourseExecution(course2, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution2)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        questionDto = new QuestionDto()
        mcqDto = new MultipleChoiceQuestionDto()
        mcqDto.setIsOrdered(false)
        questionDto.setQuestionDetailsDto(mcqDto)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
    }

    def "create an multiple choice question with an allowed role"() {

        given: "a demo teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        when:
        response = restClient.post(
                path: '/courses/'+courseExecution.getId()+'/questions/',
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct question"
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.toString()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
    }

    def "create an multiple choice question with an allowed role but a different course"() {

         given: "a demo teacher"
         createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

         when:
         response = restClient.post(
         path: '/courses/'+courseExecution2.getId()+'/questions/',
         body: questionDto,
         query: ['courseId': courseExecution2.getId()],
         requestContentType: 'application/json'
         )

         then: "the question an exception is thrown"
         def error = thrown(HttpResponseException)
         error.response.status == 403
    }

    def "create an multiple choice question with an unhallowed role"() {

         given: "a demo student"
         createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

         when:
         response = restClient.post(
         path: '/courses/'+courseExecution.getId()+'/questions/',
         body: questionDto,
         requestContentType: 'application/json'
         )

         then: "check the response status"

         def error = thrown(HttpResponseException)
         error.response.status == 403
    }

    def cleanup() {
        persistentCourseCleanup()
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        courseExecutionRepository.deleteById(courseExecution2.getId())
        courseRepository.deleteById(course2.getId())
        //questionRepository.deleteById(course.getId())
        //questionDetailsRepository.deleteById(course.getId())
    }

}