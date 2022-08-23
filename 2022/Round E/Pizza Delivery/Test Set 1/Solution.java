import java.util.Arrays;
import java.util.Scanner;

public class Solution {
  static final int[] R_OFFSETS = {-1, 0, 0, 1};
  static final int[] C_OFFSETS = {0, 1, -1, 0};

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    int T = sc.nextInt();
    for (int tc = 1; tc <= T; ++tc) {
      int N = sc.nextInt();
      int P = sc.nextInt();
      int M = sc.nextInt();
      int Ar = sc.nextInt() - 1;
      int Ac = sc.nextInt() - 1;
      char[] OP = new char[R_OFFSETS.length];
      int[] K = new int[R_OFFSETS.length];
      for (int i = 0; i < R_OFFSETS.length; ++i) {
        OP[i] = sc.next().charAt(0);
        K[i] = sc.nextInt();
      }
      int[] X = new int[P];
      int[] Y = new int[P];
      int[] C = new int[P];
      for (int i = 0; i < P; ++i) {
        X[i] = sc.nextInt() - 1;
        Y[i] = sc.nextInt() - 1;
        C[i] = sc.nextInt();
      }

      System.out.println(String.format("Case #%d: %s", tc, solve(N, X, Y, C, M, Ar, Ac, OP, K)));
    }

    sc.close();
  }

  static String solve(int N, int[] X, int[] Y, int[] C, int M, int Ar, int Ac, char[] OP, int[] K) {
    long[][] dp = new long[N][N];
    for (int r = 0; r < N; ++r) {
      Arrays.fill(dp[r], Long.MIN_VALUE);
    }
    dp[Ar][Ac] = 0;

    for (int i = 0; i < M; ++i) {
      long[][] nextDp = new long[N][];
      for (int r = 0; r < N; ++r) {
        nextDp[r] = dp[r].clone();
      }

      for (int r = 0; r < N; ++r) {
        for (int c = 0; c < N; ++c) {
          if (dp[r][c] != Long.MIN_VALUE) {
            for (int d = 0; d < R_OFFSETS.length; ++d) {
              int adjR = r + R_OFFSETS[d];
              int adjC = c + C_OFFSETS[d];
              if (adjR >= 0 && adjR < N && adjC >= 0 && adjC < N) {
                nextDp[adjR][adjC] = Math.max(nextDp[adjR][adjC], evaluate(dp[r][c], OP[d], K[d]));
              }
            }
          }
        }
      }

      dp = nextDp;
    }

    long result = 0;
    for (int r = 0; r < N; ++r) {
      for (int c = 0; c < N; ++c) {
        result = Math.max(result, dp[r][c]);
      }
    }

    return String.valueOf(result);
  }

  static long evaluate(long operand1, char operator, int operand2) {
    if (operator == '+') {
      return operand1 + operand2;
    }
    if (operator == '-') {
      return operand1 - operand2;
    }
    if (operator == '*') {
      return operand1 * operand2;
    }

    return Math.floorDiv(operand1, operand2);
  }
}