<template>
    <div ref="editor" style="height: 300px; width: 500px;"></div>
</template>
  
<script>
import ace from 'ace-builds'
import 'ace-builds/src-noconflict/theme-chrome'
import 'ace-builds/src-noconflict/mode-java'
import 'ace-builds/src-noconflict/ext-language_tools'
  
export default {
    mounted() {
      // 初始化 ACE 编辑器
      this.editor = ace.edit(this.$refs.editor)
  
      // 设置编辑器主题
      this.editor.setTheme('ace/theme/chrome')
  
      // 设置编辑器语言模式
    this.editor.session.setMode('ace/mode/java')

    // 设置默认代码
    this.editor.setValue(
`public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");
    }
}`
    )

      this.editor.setOptions({
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true
    })
    },
    beforeUnmount() {
      // 销毁 ACE 编辑器实例
      this.editor.destroy()
    }
}

</script>
  

<style scoped>
</style>