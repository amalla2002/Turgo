#include <string>
#include <iostream>
#include <vector>
#include <algorithm>
#include <queue>
#include <jni.h>
using namespace std;
extern "C" JNIEXPORT jint JNICALL
Java_com_example_turgo_fragments_ParksFragment_getInt(JNIEnv *env, jobject thiz) {
    const int INF = 1e7;
    int N = 12, targetPow2 = 1, k, target, pos, val;
    vector<int> data(N, 0), Ans;
    // make tree size nice
    for (int i = 1; targetPow2<=N; ++i) targetPow2 *= 2;
    N = targetPow2;
    // example build tree
    vector<int> segTree(N*2+24, INF);
    k = N;
    for (auto x : data) segTree[k++] = x;
    for (int i = N-1; i>0; --i) segTree[i] = min(segTree[i*2], segTree[i*2+1]);
    //example query
    queue<int> q;
    q.push(1);
    while (!q.empty()) {
        int v = q.front(); q.pop();
        if (segTree[v]>target) continue;
        if (v>=N) {Ans.push_back(v); continue;}
        q.push(v*2); q.push(v*2+1);
    }
    // return ans
    //example update
    pos += N;
    segTree[pos] = val;
    pos /= 2;
    for (; pos>0; pos/=2) segTree[pos] = min(segTree[2*pos], segTree[2*pos+1]);
    // return updated tree
    return 17;
}
extern "C" jstring
Java_com_example_turgo_fragments_ParksFragment_getText(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from c++ ");
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_ParksFragment_queueTree(JNIEnv *env, jobject thiz, jintArray tree) {
    // TODO: implement queueTree()
    // convert to easy to handle tree
    jsize size = env->GetArrayLength( tree );
    vector<int>T( size, 1e7 );
    env->GetIntArrayRegion( tree, jsize{0}, size, &T[0] );

    T[0] = 2;

    env->SetIntArrayRegion(tree, jsize {0}, size, &T[0]);
    return tree;
}