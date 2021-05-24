<template>
  <div>
    <span v-html="convertMarkDown(sQuestionDetails.answer)" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

@Component
export default class OpenAnswerViewView extends Vue {
  @Prop() readonly questionDetails!: OpenAnswerQuestionDetails;
  sQuestionDetails!: OpenAnswerQuestionDetails;


  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style>
.cm-custom-drop-down {
  background: #ffa014;
  color: white;
  font-size: x-small;
  padding: 4px 2px 4px 2px;
  border-radius: 5px;
  font-weight: bolder;
  height: 16px;
}
.code-create {
  text-align: left;
}
.CodeMirror-linenumber.CodeMirror-gutter-elt {
  left: 0;
}
</style>
