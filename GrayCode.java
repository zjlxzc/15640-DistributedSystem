public class Solution {
    public ArrayList<Integer> grayCode(int n) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ret.add(0);
        if (n == 0) {
            return ret;
        }
        int cur = 1;
        while (cur <= n) {
            int times = 1;
            for (int t = 1; t < cur; t++) {
                times *= 2;
            }
            int p = ret.size() - 1;
            while (p >= 0) {
                ret.add(ret.get(p) + times);
                p--;
            }
            cur++;
        }
        return ret;
    }
}