package ynu.ls.coloring; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TabuColoring {
      private Graph g; // 图对象
      private int nbColor; // 颜色数
      private int[] sol; // 当前解
      private int[] bestSol; // 最佳解
      private int[][] tabu; // 禁忌表
      private int[][] adjColor; // 邻接颜色表
      private Random random; // 随机数生成器

      public TabuColoring(Graph graph, int nbColor) {
            this.g = graph;
            this.nbColor = nbColor;
            this.sol = new int[g.verNum + 1]; // 当前解
            this.bestSol = new int[g.verNum + 1]; // 最佳解
            this.tabu = new int[g.verNum + 1][nbColor + 1]; // 禁忌表
            this.adjColor = new int[g.verNum + 1][nbColor + 1]; // 邻接颜色表
            this.random = new Random();
            initialize(); // 初始化解
      }  
      // 初始化解，随机分配颜色、禁忌表、邻接颜色表
      private void initialize() {
            for (int i = 1; i <= g.verNum; i++)
                  sol[i] = random.nextInt(nbColor) + 1;
            for (int v = 1; v <= g.verNum; v++)
            for (int c = 1; c <= nbColor; c++)
                  tabu[v][c] = 0; // 禁忌表初始化为 0
            for (int v = 1; v <= g.verNum; v++)
            for (int neighbor : g.getNeighbors(v))
                  adjColor[v][sol[neighbor]]++; // 统计点 v 的邻居颜色
      }
      // 计算当前解的冲突数
      private int calculateConflicts(int[] solution) {
            int conflicts = 0;
            // 冲突数为点 v 的冲突邻居数
            for (int v = 1; v <= g.verNum; v++)
                  conflicts += adjColor[v][solution[v]]; 
            return conflicts / 2; // 每条冲突边被计算了两次
      }  
      // 计算更改颜色的分数
      private int score(int v, int c1, int c2) {
            return adjColor[v][c1] - adjColor[v][c2];
      }
      // 执行禁忌搜索
      public int[] search() {
            int iter = 1; // 迭代次数
            // int maxIterations = 1000000000; // 最大迭代次数
            int bestConf = calculateConflicts(sol); // 最佳解的冲突数
            List<int[]> bestMoves = new ArrayList<>(); // 存储最佳移动的列表
            while(bestConf > 0) {
                  System.out.println("Iteration: " + iter);
                  int bestMoveScore = Integer.MIN_VALUE; // 最佳移动的分数
                  bestMoves.clear(); // 清空最佳移动列表
                  int oldConf = calculateConflicts(sol); // 当前解的矛盾边

                  for(int v=1; v<=g.verNum; v++) { // 顶点
                        int currentColor = sol[v];
                        if(adjColor[v][currentColor] == 0) continue;
                        for (int c=1; c<=nbColor; c++) { // 尝试替换的颜色
                              if(currentColor == c) continue;
                              int s = score(v, currentColor, c);
                              // 没被禁忌or能特赦 and 非负提升
                              if((tabu[v][c] < iter || oldConf - s < bestConf) && s >= bestMoveScore) {
                                    if(s > bestMoveScore) {
                                          bestMoves.clear();
                                          bestMoveScore = s;
                                    }
                                    bestMoves.add(new int[]{v, c});
                              }
                        }
                  }
                  if(!bestMoves.isEmpty()) break;
                  int[] selectedMove = bestMoves.get(random.nextInt(bestMoves.size()));
                  int v = selectedMove[0], newColor = selectedMove[1], oldColor = sol[v];
                  // 改变颜色
                  sol[v] = newColor;
                  for(int u: g.getNeighbors(v)) {
                        adjColor[u][oldColor] --;
                        adjColor[u][newColor] ++;
                  }
                  // 改变禁忌表
                  int newConf = oldConf - bestMoveScore;
                  tabu[v][oldColor] = iter + newConf + random.nextInt(nbColor);
                  iter ++;
            }

            return sol;
      } 
}