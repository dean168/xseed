<template>
  <div class="bc-dict-select">
    <el-select v-model="dict.id" :size="size" :class="className" :placeholder="placeholder">
      <el-option-group>
        <el-option v-for="so in options" :key="so.id" :label="so.name" :value="so.id"></el-option>
      </el-option-group>
    </el-select>
  </div>
</template>

<script>
import * as api from '@/api.provider'

export default {
  data () {
    return {
      dict: {},
      options: []
    }
  },
  props: {
    type: String,
    value: Object,
    size: String,
    className: String,
    placeholder: String
  },
  watch: {
    'value': {
      immediate: true,
      handler (newval, oldval) {
        this.dict.id !== newval.id && (this.dict = newval)
      }
    },
    'dict': {
      immediate: true,
      handler (newval, oldval) {
        this.value.id !== newval.id && this.$emit('input', newval)
      }
    }
  },
  created () {
    api.exchange({
        method: 'post',
        url: '/api/portal/dict/search?offset=0&limit=0',
        data: { type: this.type }
      }
    ).then(data => {
        this.options = data.result
      })
  }
}
</script>

<style scoped>
</style>
