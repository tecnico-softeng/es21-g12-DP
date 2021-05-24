export default class Option {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: boolean = false;
  order!: number;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
      this.order = jsonObj.order;
    }
  }
  set chooseOrder(order: number){
    this.order = order;
  }
}
