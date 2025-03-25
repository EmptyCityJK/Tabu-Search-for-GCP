package ynu.ls.coloring; 
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
            initializeSolution(); // 初始化解
            initializeTabu(); // 初始化禁忌表
            initializeAdjColor(); // 初始化邻接颜色表
      }  
      // 初始化解，随机分配颜色
      private void initializeSolution() {
            for (int i = 1; i <= g.verNum; i++)
                  sol[i] = random.nextInt(nbColor) + 1;
            // S* = S
            System.arraycopy(sol, 0, bestSol, 0, sol.length);
      }
      // 初始化禁忌表
      private void initializeTabu() {
            for (int v = 1; v <= g.verNum; v++)
            for (int c = 1; c <= nbColor; c++)
                  tabu[v][c] = 0; // 禁忌表初始化为 0
      }
      // 初始化邻接颜色表
      private void initializeAdjColor() {
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
      // 执行移动：将顶点 v 的颜色从 c1 改为 c2
      private void move(int v, int c1, int c2) {
            // 更新邻接颜色表
            for (int neighbor : g.getNeighbors(v)) {
                  adjColor[neighbor][c1]--; // 减少 c1 色的邻居数
                  adjColor[neighbor][c2]++; // 增加 c2 色的邻居数
            }
            sol[v] = c2; // 更改颜色
      }
      // 执行禁忌搜索
      public int[] search() {
            int iter = 1; // 迭代次数
            // int maxIterations = 1000000000; // 最大迭代次数
            int bestConflicts = calculateConflicts(bestSol); // 最佳解的冲突数
            while(bestConflicts > 0) {
                  System.out.println(iter);
                  int bestMoveV = -1; // 最佳移动的顶点
                  int bestMoveC = -1; // 最佳移动的颜色
                  int bestMoveScore = Integer.MIN_VALUE; // 最佳移动的分数
                  boolean foundTabuMove = false; // 是否找到满足禁忌条件的移动
                  int oldConf = calculateConflicts(sol); // 当前解的矛盾边

                  for(int v=1; v<=g.verNum; v++) { // 顶点
                        int currentColor = sol[v];
                        for (int c=1; c<=nbColor; c++) { // 尝试替换的颜色
                              if(currentColor == c) continue;
                              int s = score(v, currentColor, c);
                              // 被禁忌但特赦
                              if(tabu[v][c] >= iter && oldConf - s < bestConflicts) {
                                    bestMoveV = v;
                                    bestMoveC = c;
                                    foundTabuMove = true;
                                    break; // 特赦优先
                              }
                              // 未被特赦：选择分数最高的非禁忌移动
                              if(!foundTabuMove && tabu[v][c] < iter && s > bestMoveScore) {
                                    bestMoveV = v;
                                    bestMoveC = c;
                                    bestMoveScore = s; 
                              }
                        }
                  }

                  // 未找到最佳移动
                  if(bestMoveV == -1) {
                        break;
                  }

                  // 执行move(bestMoveV, c1, c2)
                  int curColor = sol[bestMoveV];
                  move(bestMoveV, curColor, bestMoveC);
                  int newConf = oldConf - bestMoveScore;
                  // 更新禁忌表
                  tabu[bestMoveV][curColor] = newConf + random.nextInt(curColor);
                  // 更新解
                  if(newConf < bestConflicts) {
                        bestConflicts = newConf;
                        System.arraycopy(sol, 0, bestSol, 0, sol.length);
                  }
                  iter ++;
            }

            return bestSol;
      } 
}

