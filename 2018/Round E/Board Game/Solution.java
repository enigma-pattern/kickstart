// https://codingcompetitions.withgoogle.com/kickstart/round/0000000000050ff5/0000000000051184#analysis
// https://www.topcoder.com/thrive/articles/Binary%20Indexed%20Trees#2d

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class Solution {
  static final int BATTLEFIELD_NUM = 3;
  static final int MIN_N = 3;
  static final int MAX_N = 5;
  static final int MAX_VALUE = 3003;

  static int[][] tree = new int[MAX_VALUE + 2][MAX_VALUE + 2];
  static List<int[]>[] indicesLists;

  public static void main(String[] args) {
    buildIndicesLists();

    Scanner sc = new Scanner(System.in);

    int T = sc.nextInt();
    for (int tc = 1; tc <= T; ++tc) {
      int N = sc.nextInt();
      int[] A = new int[BATTLEFIELD_NUM * N];
      for (int i = 0; i < A.length; ++i) {
        A[i] = sc.nextInt();
      }
      int[] B = new int[BATTLEFIELD_NUM * N];
      for (int i = 0; i < B.length; ++i) {
        B[i] = sc.nextInt();
      }

      System.out.println(String.format("Case #%d: %.9f", tc, solve(N, A, B)));
    }

    sc.close();
  }

  @SuppressWarnings("unchecked")
  static void buildIndicesLists() {
    indicesLists = new List[MAX_N + 1];
    for (int i = MIN_N; i <= MAX_N; ++i) {
      indicesLists[i] = buildIndicesList(i);
    }
  }

  static double solve(int N, int[] A, int[] B) {
    int[][] aSumArrays = buildSumArrays(N, A);
    int[][] bSumArrays = buildSumArrays(N, B);

    NavigableMap<Integer, Integer> sumToValue = buildSumToValue(bSumArrays);
    int[][] aValueArrays = buildValueArrays(aSumArrays, sumToValue);
    int[][] bValueArrays = buildValueArrays(bSumArrays, sumToValue);

    int[] winNums = new int[aSumArrays.length];

    for (int p = 0; p < BATTLEFIELD_NUM; ++p) {
      init(tree);
      for (int[] bValueArray : bValueArrays) {
        update(tree, bValueArray[p], bValueArray[(p + 1) % BATTLEFIELD_NUM], 1);
      }
      for (int i = 0; i < winNums.length; ++i) {
        winNums[i] +=
            read(tree, aValueArrays[i][p] - 1, aValueArrays[i][(p + 1) % BATTLEFIELD_NUM] - 1);
      }
    }

    init(tree);
    int bIndex = 0;
    for (int i = 0; i < winNums.length; ++i) {
      while (bIndex != bValueArrays.length && bValueArrays[bIndex][0] < aValueArrays[i][0]) {
        update(tree, bValueArrays[bIndex][1], bValueArrays[bIndex][2], 1);
        ++bIndex;
      }

      winNums[i] -= 2 * read(tree, aValueArrays[i][1] - 1, aValueArrays[i][2] - 1);
    }

    return (double) Arrays.stream(winNums).max().getAsInt() / bValueArrays.length;
  }

  static void init(int[][] tree) {
    for (int i = 0; i < tree.length; ++i) {
      Arrays.fill(tree[i], 0);
    }
  }

  static NavigableMap<Integer, Integer> buildSumToValue(int[][] bSumArrays) {
    List<Integer> sorted = new ArrayList<>();
    Set<Integer> seen = new HashSet<>();
    for (int[] bSumArray : bSumArrays) {
      for (int bSum : bSumArray) {
        if (!seen.contains(bSum)) {
          sorted.add(bSum);
          seen.add(bSum);
        }
      }
    }
    Collections.sort(sorted);

    NavigableMap<Integer, Integer> result = new TreeMap<>();
    for (int i = 0; i < sorted.size(); ++i) {
      result.put(sorted.get(i), i + 1);
    }

    return result;
  }

  static int[][] buildValueArrays(int[][] sumArrays, NavigableMap<Integer, Integer> sumToValue) {
    for (int[] sumArray : sumArrays) {
      for (int i = 0; i < sumArray.length; ++i) {
        Entry<Integer, Integer> entry = sumToValue.ceilingEntry(sumArray[i]);
        if (entry == null) {
          sumArray[i] = MAX_VALUE + 1;
        } else {
          sumArray[i] = entry.getValue();
        }
      }
    }

    return sumArrays;
  }

  static int read(int[][] tree, int x, int y) {
    int sum = 0;
    while (x > 0) {
      sum += readY(tree, x, y);
      x -= x & -x;
    }

    return sum;
  }

  static int readY(int[][] tree, int x, int y) {
    int sum = 0;
    while (y > 0) {
      sum += tree[x][y];
      y -= y & -y;
    }

    return sum;
  }

  static void update(int[][] tree, int x, int y, int val) {
    while (x < tree.length) {
      updateY(tree, x, y, val);
      x += x & -x;
    }
  }

  static void updateY(int[][] tree, int x, int y, int val) {
    while (y < tree[x].length) {
      tree[x][y] += val;
      y += y & -y;
    }
  }

  static List<int[]> buildIndicesList(int n) {
    List<int[]> indicesList = new ArrayList<>();
    search(indicesList, n, IntStream.range(0, 3 * n).toArray(), 0);

    return indicesList;
  }

  static void search(List<int[]> indicesList, int n, int[] indices, int depth) {
    if (depth == indices.length) {
      indicesList.add(Arrays.copyOf(indices, indices.length));

      return;
    }

    for (int i = depth; i < indices.length; ++i) {
      if (depth % n == 0 || indices[i] >= indices[depth - 1]) {
        swap(indices, i, depth);
        search(indicesList, n, indices, depth + 1);
        swap(indices, i, depth);
      }
    }
  }

  static int[][] buildSumArrays(int N, int[] values) {
    int[][] sumArrays = new int[indicesLists[N].size()][BATTLEFIELD_NUM];
    for (int i = 0; i < sumArrays.length; ++i) {
      for (int j = 0; j < 3 * N; ++j) {
        sumArrays[i][j / N] += values[indicesLists[N].get(i)[j]];
      }
    }

    Arrays.sort(sumArrays, Comparator.comparing(sumArray -> sumArray[0]));

    return sumArrays;
  }

  static void swap(int[] a, int index1, int index2) {
    int temp = a[index1];
    a[index1] = a[index2];
    a[index2] = temp;
  }
}
