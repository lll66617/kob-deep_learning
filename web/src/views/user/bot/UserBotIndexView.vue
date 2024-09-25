<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%">我的Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">创建Bot</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="add-bot-title" class="form-label">名称</label>
                                        <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-description" class="form-label">简介</label>
                                        <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-code" class="form-label">代码</label>
                                        <VAceEditor
                                            v-model:value="botadd.content"
                                            @init="editorInit"
                                            lang="c_cpp"
                                            theme="textmate"
                                            style="height: 300px" />
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class="error-message">{{ botadd.error_message }}</div>
                                    <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>

                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">创建Bot</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">名称</label>
                                                        <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-description" class="form-label">简介</label>
                                                        <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">代码</label>
                                                        <VAceEditor
                                                            v-model:value="bot.content"
                                                            @init="editorInit"
                                                            lang="c_cpp"
                                                            theme="textmate"
                                                            style="height: 300px" />
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error-message">{{ bot.error_message }}</div>
                                                    <button type="button" class="btn btn-primary" @click="update_bot(bot)">保存修改</button>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

export default {
    components: {
        VAceEditor
    },
    setup() {
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

        const store = useStore();
        let bots = ref([]);

        const botadd = reactive({
            title: "",
            description: "",
            content:"package com.kob.botrunningsystem.utils;\n\n"
                + "import java.util.*;\n"
                + "import ai.onnxruntime.*;//支持使用onnx模型"
                + "\n\n"
                + "public class Bot implements com.kob.botrunningsystem.utils.BotInterface {\n"
                + "    static class Cell {\n"
                + "        public int x, y;\n"
                + "        public Cell(int x, int y) {\n"
                + "            this.x = x;\n"
                + "            this.y = y;\n"
                + "        }\n"
                + "    }\n\n"
                + "    private boolean check_tail_increasing(int step) { \n"
                + "        if (step <= 10) return true;\n"
                + "        return step % 3 == 1;\n"
                + "    }\n\n"
                + "    public List<Cell> getCells(int sx, int sy, String steps) {\n"
                + "        \n"
                + "        steps = steps.substring(1, steps.length() - 1);\n"
                + "        List<Cell> res = new ArrayList<>();\n\n"
                + "        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};\n"
                + "        int x = sx, y = sy;\n"
                + "        int step = 0;\n"
                + "        res.add(new Cell(x, y));\n"
                + "        for (int i = 0; i < steps.length(); i ++ ) {\n"
                + "            int d = steps.charAt(i) - '0';\n"
                + "            x += dx[d];\n"
                + "            y += dy[d];\n"
                + "            res.add(new Cell(x, y));\n"
                + "            if (!check_tail_increasing( ++ step)) {\n"
                + "                res.remove(0);\n"
                + "            }\n"
                + "        }\n"
                + "        return res;\n"
                + "    }\n\n"
                + "    @Override\n"
                + "    public Integer nextMove(String input) {\n"
                + "        String[] strs = input.split(\"#\");\n"
                + "        int[][] g = new int[13][14];\n"
                + "        for (int i = 0, k = 0; i < 13; i ++ ) {\n"
                + "            for (int j = 0; j < 14; j ++, k ++ ) {\n"
                + "                if (strs[0].charAt(k) == '1') {\n"
                + "                    g[i][j] = 1;\n"
                + "                }\n"
                + "            }\n"
                + "        }//地图,g[x][y]=1为障碍物\n\n"
                + "        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);//自己的起始位置\n"
                + "        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);//对手的起始位置\n\n"
                + "        List<Cell> aCells = getCells(aSx, aSy, strs[3]);//自己所占单元格\n"
                + "        List<Cell> bCells = getCells(bSx, bSy, strs[6]);//对手所占单元格\n\n"
                + "        //将自己和对手所占单元格标记为障碍物\n"
                + "        for (Cell c: aCells) g[c.x][c.y] = 1;\n"
                + "        for (Cell c: bCells) g[c.x][c.y] = 1;\n\n"
                + "        //请在下面创造您的ai\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n",
            error_message: "",
        });

        const editorInit = (editor) => {
        // 启用基本代码补全
        editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true
        });

        // 设置编辑器主题
        editor.setTheme('ace/theme/chrome')
        // 设置编辑器语言模式
        editor.session.setMode('ace/mode/java')
        
        };

        const refresh_bots = () => {
            $.ajax({
                url: "https://app7159.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        }

        refresh_bots();

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "https://app7159.acapp.acwing.com.cn/api/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "https://app7159.acapp.acwing.com.cn/api/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        Modal.getInstance('#update-bot-modal-' + bot.id).hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "https://app7159.acapp.acwing.com.cn/api/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
            update_bot,
            remove_bot,
            editorInit
        }
    }
}
</script>

<style scoped>
div.error-message {
    color: red;
}

</style>