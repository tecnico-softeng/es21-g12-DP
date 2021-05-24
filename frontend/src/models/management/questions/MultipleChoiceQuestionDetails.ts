import Option from '@/models/management/Option';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class MultipleChoiceQuestionDetails extends QuestionDetails {
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  ordered: boolean = false;
  numberOfOrdered: number = 0;

  constructor(jsonObj?: MultipleChoiceQuestionDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.ordered = jsonObj.ordered;
      this.numberOfOrdered = jsonObj.numberOfOrdered;
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }

  setAsNew(): void {
    this.options.forEach(option => {
      option.id = null;
    });
    this.ordered = false;
    this.numberOfOrdered = 0;
  }

  changeNumberOfOrdered(n: number): void {
    this.numberOfOrdered += n;
  }
}
