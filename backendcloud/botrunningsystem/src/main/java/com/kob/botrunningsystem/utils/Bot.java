package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import ai.onnxruntime.*;//支持使用onnx模型

//因为后面要变成用户传过来
public class Bot implements java.util.function.Supplier<Integer> {
    int[][] g = new int[13][14];//地图
    AI ai=new AI();

    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }//一个单元格

    private boolean check_tail_increasing(int step) {  // 检验当前回合，蛇的长度是否增加
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {//将蛇的身体取出来
        //第一段地图#自己起始x坐标#自己起始y坐标#我的操作#对手起始x坐标#对手起始y坐标#对手操作
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i ++ ) {
            int d = steps.charAt(i) - '0';
            //System.out.println("direction:"+d);
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing( ++ step)) {
                res.remove(0);
            }//如果蛇的长度不增加删除尾部节点
        }
        return res;
    }

    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        for (int i = 0, k = 0; i < 13; i ++ ) {
            for (int j = 0; j < 14; j ++, k ++ ) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }//地图,g[x][y]=1为障碍物

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);//玩家A的起始位置
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);//玩家B的起始位置

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);//玩家A所占单元格
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);//玩家B所占单元格

        int direction = strs[3].charAt(strs[3].length()-2)-'0';
        if(direction==-8){
            if(aSx==1)direction=2;
            else direction=0;
        }//玩家目前所朝方向
        //System.out.println("last_direction:"+direction);

        //将两名玩家所占单元格标记为障碍物
        for (Cell c: aCells) g[c.x][c.y] = 1;
        for (Cell c: bCells) g[c.x][c.y] = 1;

        /*int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i ++ ) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }*/

        //准备工作就绪，开始DQN_AI代码的正式部署！
        float[] input_data=new float[8];//输入ai的数据
        float dr_l=direction==3?1.f:0.f;//朝左
        float dr_r=direction==1?1.f:0.f;//朝右
        float dr_u=direction==0?1.f:0.f;//朝上
        float dr_d=direction==2?1.f:0.f;//朝下

        //蛇头目前所处位置
        int x = aCells.get(aCells.size() - 1).x;
        int y = aCells.get(aCells.size() - 1).y;

        int[] dx = {0, 0, -1, 1}, dy = {-1, 1, 0, 0};

        //前方是否有危险
        if(dr_l==1&&is_collision(x+dx[0],y+dy[0]))input_data[0]=1.f;
        else if(dr_r==1&&is_collision(x+dx[1],y+dy[1]))input_data[0]=1.f;
        else if(dr_u==1&&is_collision(x+dx[2],y+dy[2]))input_data[0]=1.f;
        else if(dr_d==1&&is_collision(x+dx[3],y+dy[3]))input_data[0]=1.f;
        else input_data[0]=0.f;
        //右方是否有危险
        if(dr_d==1&&is_collision(x+dx[0],y+dy[0]))input_data[1]=1.f;
        else if(dr_u==1&&is_collision(x+dx[1],y+dy[1]))input_data[1]=1.f;
        else if(dr_l==1&&is_collision(x+dx[2],y+dy[2]))input_data[1]=1.f;
        else if(dr_r==1&&is_collision(x+dx[3],y+dy[3]))input_data[1]=1.f;
        else input_data[1]=0.f;
        //左方是否有危险
        if(dr_u==1&&is_collision(x+dx[0],y+dy[0]))input_data[2]=1.f;
        else if(dr_d==1&&is_collision(x+dx[1],y+dy[1]))input_data[2]=1.f;
        else if(dr_r==1&&is_collision(x+dx[2],y+dy[2]))input_data[2]=1.f;
        else if(dr_l==1&&is_collision(x+dx[3],y+dy[3]))input_data[2]=1.f;
        else input_data[2]=0.f;
        //是否朝左
        input_data[3]=dr_l;
        //是否朝右
        input_data[4]=dr_r;
        //是否朝上
        input_data[5]=dr_u;
        //是否朝下
        input_data[6]=dr_d;
        //长度是否增加
        input_data[7]=check_tail_increasing(strs[3].length()-2)?1.f:0.f;
        for(int i=0;i<8;i++){
            System.out.print(input_data[i]+" ");
        }

        try {
            int result = ai.getResult(input_data);
            System.out.println(result);
            //朝前
            if(result==0)return direction;
            //朝左
            if(result==2){
                if(dr_l==1)return 2;
                if(dr_r==1)return 0;
                if(dr_u==1)return 3;
                if(dr_d==1)return 1;
            }
            //朝右
            if(result==1){
                if(dr_l==1)return 0;
                if(dr_r==1)return 2;
                if(dr_u==1)return 1;
                if(dr_d==1)return 3;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    //是否发生碰撞
    public boolean is_collision(int x,int y){
        if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc=new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
