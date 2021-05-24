
describe('Manage Open Answer Questions Walk-through', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.logout();
  });

  it('Teacher creates a new open answer question', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.createOpenAnswerQuestion('ABCD_1','who_s first?','A',true)
  });

  it('Teacher can view an open answser question (with button)', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.viewOpenAnswerQuestionButton('ABCD_1')
  });

  it('Teacher can view an open answser question (with click)', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.viewOpenAnswerQuestionClick('ABCD_1')
  });

  it('Teacher can update title (with click)', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.updateOpenAnswerQuestionClick('ABCD_1','ABCD_2')
  });

  it('Teacher can update content (with button)', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.updateOpenAnswerQuestionButton('ABCD_2', 'who_s second?')
  });

  it('Teacher can duplicate question', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.duplicateOpenAnswerQuestion('ABCD_2')
  });

  it('Teacher can delete question', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;
    cy.deleteQuestion('ABCD_2')
  });



});
