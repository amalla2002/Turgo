// TODO: NOTE THE +24 TO THE SIZE

#include <string>
#include <iostream>
#include <vector>
#include <algorithm>
#include <queue>
#include <jni.h>
using namespace std;
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_ParksFragment_queueTree(JNIEnv *env, jobject thiz, jintArray tree,
                                                         jint target) {
    jsize size = env->GetArrayLength( tree );
    vector<int>T( size ), Ans;
    env->GetIntArrayRegion( tree, jsize{0}, size, &T[0] );
    queue<int> q;
    int x = target;
    q.push(1);
    while (!q.empty()) {
        int v = q.front(); q.pop();
        if (T[v]>x) continue;
        if (v>=(T.size()-24)/2) {
            cout << v << " "  << v-(T.size()-24)/2 << " " << T[v] << "\n";
            Ans.push_back(v-(T.size()-24)/2);
            continue;
        }
        q.push(v*2); q.push(v*2+1);
    }
    jintArray ans = env->NewIntArray(Ans.size());
    env->SetIntArrayRegion(ans, jsize{0}, env->GetArrayLength(ans),&Ans[0]);
    return ans;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_MainActivity_buildTree(JNIEnv *env, jobject thiz, jintArray people) {
    const int INF = 1e7;
    jsize size = env->GetArrayLength( people );
    vector<int> A(size);
    env->GetIntArrayRegion( people, jsize{0}, size, &A[0] ); // fills A with people
    int pow2 = 1; for (int i = 1; pow2<A.size(); ++i) pow2*=2;
    vector<int> segTree(pow2*2+24, INF);
    for (int i = 0, k = pow2; i<A.size(); ++i, ++k) segTree[k] = 0;
    for (int i = pow2-1; i>0; --i) segTree[i] = min(segTree[i*2], segTree[i*2+1]);
    jintArray ans = env->NewIntArray(segTree.size());
    env->SetIntArrayRegion(ans, jsize{0}, env->GetArrayLength(ans),&segTree[0]);
    return ans;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_VisitParkFragment_updateTree(JNIEnv *env, jobject thiz,
                                                              jintArray tree, jint pos, jint val) {
    jsize size = env->GetArrayLength( tree );
    vector<int> T(size);
    env->GetIntArrayRegion(tree, jsize{0}, size, &T[0]);
    pos += (T.size()-24)/2;
    T[pos] += val; // not using T[pos] = val in case there are multiple updates at the same time
    pos /= 2;
    for (; pos>0; pos/=2) T[pos] = min(T[2*pos], T[2*pos+1]);
    env->SetIntArrayRegion(tree, jsize{0}, size, &T[0]);
    return tree;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_PlacesFragment_updateTree(JNIEnv *env, jobject thiz,
                                                              jintArray tree, jint pos, jint val) {
    jsize size = env->GetArrayLength( tree );
    vector<int> T(size);
    env->GetIntArrayRegion(tree, jsize{0}, size, &T[0]);
    pos += (T.size()-24)/2;
    T[pos] += val; // not using T[pos] = val in case there are multiple updates at the same time
    pos /= 2;
    for (; pos>0; pos/=2) T[pos] = min(T[2*pos], T[2*pos+1]);
    env->SetIntArrayRegion(tree, jsize{0}, size, &T[0]);
    return tree;
}
