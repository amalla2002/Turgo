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

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_FlightsFragment_findBestCombination(JNIEnv *env, jobject thiz,
                                                                     jdoubleArray going_price_flights,
                                                                     jdoubleArray returning_price_flights,
                                                                     jdoubleArray hotel_price,
                                                                     jintArray going_indices,
                                                                     jintArray returning_indices) {
    // TODO: implement findBestCombination()
    // assign
    jsize size = env->GetArrayLength( going_price_flights ), size1 = env->GetArrayLength( going_indices), size2 = env->GetArrayLength( returning_indices);
    vector<int> dates(2), starts(size1), ends(size2); // going and returning.
    vector<double> ori(size), dest(size), stay(size), p(size+4, 0);
    env->SetDoubleArrayRegion(going_price_flights, jsize{0}, size, &ori[0]);
    env->SetDoubleArrayRegion(returning_price_flights, jsize{0}, size, &dest[0]);
    env->SetDoubleArrayRegion(hotel_price, jsize{0}, size, &stay[0]);
    env->SetIntArrayRegion(going_indices, jsize{0}, size1, &starts[0]);
    env->SetIntArrayRegion(returning_indices, jsize{0}, size2, &ends[0]);

    // build prefix sum
    for (int i = 1; i<384*2+2; ++i) p[i] = p[i-1]+stay[i-1];



    jintArray ans = env->NewIntArray(dates.size());
    env->SetIntArrayRegion(ans, jsize{0}, env->GetArrayLength(ans), &dates[0]);
    return ans;

}