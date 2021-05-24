import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerQuestionDetails extends QuestionDetails {
  regex: boolean = false;
  answer: string = '';

  constructor(jsonObj?: OpenAnswerQuestionDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.regex = jsonObj.regex || this.regex;
      this.answer = jsonObj.answer || this.answer;
    }
  }

  setAsNew(): void {  }
}
