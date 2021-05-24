package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportOpenAnswerQuestionWebServiceIT extends SpockTest {
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
    def openAnswerQuestionDto

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
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer(ANSWER_1_CONTENT)
        openAnswerQuestionDto.setRegex(true)
        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)
    }

    def 'export to xml with an allowed role'() {
         given: "a demo teacher"
         createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

         when:
         def response = restClient.get
         path: '/courses/'+courseExecution.getId()+'/questions/export',
         requestContentType: 'application/json'
         )

         then: "check the response status"
         response != null
         response.status == 200
    }

    def 'export to xml with an unhallowed role'() {
        given: "a demo student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        response = restClient.get(
                path: '/courses/'+courseExecution.getId()+'/questions/export',
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
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
    }
}
