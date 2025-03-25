package ynu.ls.coloring; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TabuColoring {
      private Graph g; // 图对象
      private int nbColor; // 颜色数
      private int conf; // 冲突数
      private int bestConf; // 最少冲突数
      private int[] sol; // 当前解
      private int[] bestSol; // 最优解
      private int[][] tabu; // 禁忌表
      private int[][] adjColor; // 邻接颜色表
      private Random random; // 随机数生成器
      private boolean flg; // 是否能找到

      public TabuColoring(Graph graph, int nbColor) {
            this.g = graph;
            this.nbColor = nbColor;
            this.sol = new int[g.verNum + 1]; // 当前解
            this.bestSol = new int[g.verNum + 1];
            this.tabu = new int[g.verNum + 1][nbColor + 1]; // 禁忌表
            this.adjColor = new int[g.verNum + 1][nbColor + 1]; // 邻接颜色表
            this.random = new Random();
            initialize(); // 初始化解
      }  
      // 初始化解，随机分配颜色、禁忌表、邻接颜色表
      private void initialize() {
            for (int i = 1; i <= g.verNum; i++)
                  sol[i] = random.nextInt(nbColor) + 1;
            for (int v = 1; v <= g.verNum; v++) {
                  for (int neighbor : g.getNeighbors(v)) {
                        adjColor[v][sol[neighbor]]++; // 统计点 v 的邻居颜色
                        if(v < neighbor && sol[neighbor] == sol[v]) conf ++;
                  }
            }
            bestConf = conf;
            flg = false;
      }
      // 执行禁忌搜索
      public int[] search() {
            if(flg) return bestSol.clone();
            int iter = 0; // 迭代次数
            while(conf > 0) {
                  int bestMoveScore = Integer.MIN_VALUE; // 最佳移动的分数
                  // bestMoves.clear(); // 清空最佳移动列表
                  List<int[]> bestMoves = new ArrayList<>(); // 存储最佳移动的列表
                  for(int v=1; v<=g.verNum; v++) { // 顶点
                        int currentColor = sol[v];
                        if(adjColor[v][currentColor] == 0) continue;
                        for (int c=1; c<=nbColor; c++) { // 尝试替换的颜色
                              if(currentColor == c) continue;
                              int score = adjColor[v][currentColor] - adjColor[v][c];
                              // 没被禁忌or能特赦 and 非负提升
                              if((tabu[v][c] < iter || conf - score < bestConf) && score >= bestMoveScore) {
                                    if(score > bestMoveScore) {
                                          bestMoves.clear();
                                          bestMoveScore = score;
                                    }
                                    bestMoves.add(new int[]{v, c});
                              }
                        }
                  }
                  if(bestMoves.isEmpty()) break;
                  int[] selectedMove = bestMoves.get(random.nextInt(bestMoves.size()));
                  int v = selectedMove[0], newColor = selectedMove[1], oldColor = sol[v];
                  // 改变颜色
                  sol[v] = newColor;
                  // 改变禁忌表
                  conf = conf - bestMoveScore;
                  bestConf = Math.min(conf, bestConf);
                  tabu[v][oldColor] = iter + conf + random.nextInt(10);
                  for(int u: g.getNeighbors(v)) {
                        adjColor[u][oldColor] --;
                        adjColor[u][newColor] ++;
                  }
                  iter ++;
            }
            System.out.println("Iteration: " + iter);
            flg = true;
            bestSol = sol.clone();
            return bestSol.clone();
      } 
}