

<template>
  <div>

    <v-row>
      <v-col cols="10">
        <v-textarea
        v-model="sQuestionDetails.answer"
        label="Answer"
        :rules="[v => !!v || 'Answer content is required']"
        auto-grow
        required
        data-cy="questionAnswerTextArea"
        rows="4"
        ></v-textarea>
      </v-col>
      <v-col cols="1">
        <v-switch
          v-model="sQuestionDetails.regex"
          label='Regex'
          inset
          data-cy="regexSwitch"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">

import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

@Component
export default class OpenAnswerCreate extends Vue {
  @PropSync('questionDetails', { type: OpenAnswerQuestionDetails })
  sQuestionDetails!: OpenAnswerQuestionDetails;
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
