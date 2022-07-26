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
    // moving things into normal cpp types
    jsize size = env->GetArrayLength( tree );
    vector<int> minimumSegmentedTree( size ), Ans;
    const int segmentationFaultPrecaution = 24;
    env->GetIntArrayRegion( tree, jsize{0}, size, &minimumSegmentedTree[0] );

    // normal BFS
    queue<int> q;
    int noMoreThanThisAmountOfPeople = target;
    q.push(1);
    while (!q.empty()) {
        int v = q.front(); q.pop();
        if (minimumSegmentedTree[v]>noMoreThanThisAmountOfPeople) continue;
        if (v>=(minimumSegmentedTree.size()-segmentationFaultPrecaution)/2) { // is in last row of tree (data)
            Ans.push_back(v-(minimumSegmentedTree.size()-segmentationFaultPrecaution)/2);
            continue;
        }
        q.push(v*2); q.push(v*2+1); // adds the current nodes' childs
    }

    //converts answers back to java types
    jintArray ans = env->NewIntArray(Ans.size());
    env->SetIntArrayRegion(ans, jsize{0}, env->GetArrayLength(ans),&Ans[0]);
    return ans;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_MainActivity_buildTree(JNIEnv *env, jobject thiz, jintArray people) {
    const int INF = 1e7;
    // move things to normal cpp types
    jsize size = env->GetArrayLength( people );
    vector<int> data(size);
    const int segmentationFaultPrecaution = 24;
    env->GetIntArrayRegion( people, jsize{0}, size, &data[0] ); // fills A with people

    // looks for a power of 2 for the size of tree
    int pow2 = 1; for (int i = 1; pow2<data.size(); ++i) pow2*=2;

    // build tree
    vector<int> minimumSegmentedTree(pow2*2+segmentationFaultPrecaution, INF);
    for (int i = 0, k = pow2; i<data.size(); ++i, ++k) minimumSegmentedTree[k] = 0;
    for (int i = pow2-1; i>0; --i) minimumSegmentedTree[i] = min(minimumSegmentedTree[i*2], minimumSegmentedTree[i*2+1]);

    // moves answers back to java types
    jintArray ans = env->NewIntArray(minimumSegmentedTree.size());
    env->SetIntArrayRegion(ans, jsize{0}, env->GetArrayLength(ans),&minimumSegmentedTree[0]);
    return ans;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_VisitParkFragment_updateTree(JNIEnv *env, jobject thiz,
                                                              jintArray tree, jint pos, jint val) {
    // move things to normal cpp types
    jsize size = env->GetArrayLength( tree );
    vector<int> minimumSegmentedTree(size);
    const int segmentationFaultPrecaution = 24;
    env->GetIntArrayRegion(tree, jsize{0}, size, &minimumSegmentedTree[0]);

    // get position of data in tree
    pos += (minimumSegmentedTree.size()-segmentationFaultPrecaution)/2;
    minimumSegmentedTree[pos] += val; // not using T[pos] = val in case there are multiple updates at the same time

    // start at the parent and go to the root recalculating min
    pos /= 2;
    for (; pos>0; pos/=2) minimumSegmentedTree[pos] = min(minimumSegmentedTree[2*pos], minimumSegmentedTree[2*pos+1]);

    // move things back to java types
    env->SetIntArrayRegion(tree, jsize{0}, size, &minimumSegmentedTree[0]);
    return tree;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_turgo_fragments_PlacesFragment_updateTree(JNIEnv *env, jobject thiz,
                                                              jintArray tree, jint pos, jint val) {
    // see above function
    jsize size = env->GetArrayLength( tree );
    vector<int> minimumSegmentedTree(size);
    const int segmentationFaultPrecaution = 24;

    env->GetIntArrayRegion(tree, jsize{0}, size, &minimumSegmentedTree[0]);
    pos += (minimumSegmentedTree.size()-segmentationFaultPrecaution)/2;
    minimumSegmentedTree[pos] += val; // not using T[pos] = val in case there are multiple updates at the same time
    pos /= 2;
    for (; pos>0; pos/=2) minimumSegmentedTree[pos] = min(minimumSegmentedTree[2*pos], minimumSegmentedTree[2*pos+1]);
    env->SetIntArrayRegion(tree, jsize{0}, size, &minimumSegmentedTree[0]);
    return tree;
}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_example_turgo_fragments_FlightsFragment_findBestCombination(JNIEnv *env, jobject thiz,
                                                                     jdoubleArray going_price_flights, jdoubleArray returning_price_flights, jdoubleArray hotel_price,
                                                                     jintArray going_indices, jintArray returning_indices,
                                                                     jint min_days, jint max_days) {
    // move things to normal cpp types and prepare data containers
    jsize size = env->GetArrayLength( going_price_flights );
    jsize size1 = env->GetArrayLength( going_indices );
    int minDays = min_days, maxDays = max_days;
    vector<int>  starts(2), ends(2); // going and returning.
    vector<double> origin(size), destination(size), stay(size), dynamicProgramming(size+4), answer(3, 1e9+7);
    env->GetDoubleArrayRegion(going_price_flights, jsize{0}, size, &origin[0]);
    env->GetDoubleArrayRegion(returning_price_flights, jsize{0}, size, &destination[0]);
    env->GetDoubleArrayRegion(hotel_price, jsize{0}, size, &stay[0]);
    env->GetIntArrayRegion(going_indices, jsize{0}, size1, &starts[0]);
    env->GetIntArrayRegion(returning_indices, jsize{0}, size1, &ends[0]);

    // get sums from 0 to ith for all i
    dynamicProgramming[starts[0]-1] = 0;
    for (int i = starts[0]; i<=ends[1]; ++i) dynamicProgramming[i] = dynamicProgramming[i-1] + stay[i-1];

    // check starts with ends and add costs
    for (int i = starts[0]; i<=starts[1]; ++i) {
        for (int j = ends[0]; j<=ends[1]; ++j) {
            if (i>=j) continue; // arrive before depart
            if (!(j-(i+1)>=minDays && j-(i+1)<=maxDays)) continue; // between day range
            double sum = origin[i]+destination[j]+(dynamicProgramming[j]-dynamicProgramming[i]); // Assuming hotel price is in USD
            if (sum<answer[0]) {
                answer[0] = sum;
                answer[1] = i;
                answer[2] = j;
            }
            else if (sum==answer[0] && (answer[2]-answer[1]<j-i)) { //if you can get more days for same price, take
                answer[0] = sum;
                answer[1] = i;
                answer[2] = j;
            }
        }
    }

    // move things back to java
    jdoubleArray ans = env->NewDoubleArray(answer.size());
    env->SetDoubleArrayRegion(ans, jsize{0}, env->GetArrayLength(ans), &answer[0]);
    return ans;
}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_example_turgo_NotificationSettingActivity_computeWeightAndBias(JNIEnv *env, jobject thiz,
                                                                        jintArray X_train_in,
                                                                        jintArray y_train_in,
                                                                        jdouble weight_in,
                                                                        jdouble bias_in) {
    jsize m_in = env->GetArrayLength(X_train_in);
    int m = m_in; // number of training samples
    // move things to normal cpp types and prepare data containers
    vector<double> X_train(m), y_train(m);
    env->GetDoubleArrayRegion(reinterpret_cast<jdoubleArray>(X_train_in), jsize{0}, m_in, &X_train[0]);
    env->GetDoubleArrayRegion(reinterpret_cast<jdoubleArray>(y_train_in), jsize{0}, m_in, &y_train[0]);
    double weight = weight_in, bias = bias_in, alpha = 0.003; // alpha is the learning rate and was tested

    vector<double> J; // will store cost of each iteration

    // iterations of gradient descent
    for (int iteration = 0; iteration<10000; ++iteration) {

        double total_cost = 0.0;
        double dj_dw = 0.0, dj_db = 0.0; // derivative of function J with respect to weights and bias

        for (int i = 0; i<m; ++i) {
            // get prediction
            double z_i;
            z_i = X_train[i]*weight+bias;

            // truncate with sigmoid function
            double f_wb_i = 1/(1+ exp(-z_i));

            // compute loss
            double loss = -y_train[i]*log(f_wb_i) - (1-y_train[i])* log(1-f_wb_i);
            total_cost += loss;

            // compute gradient
            dj_dw += (f_wb_i-y_train[i])*X_train[i];
            dj_db += (f_wb_i-y_train[i]);
        }
        dj_dw /= m;
        dj_db /= m;
        total_cost /= m;

        weight = weight - alpha*dj_dw;
        bias   = bias   - alpha*dj_db;

        J.push_back(total_cost);
    }

    vector<double> ans = {weight, bias};
    jsize size = ans.size();
    jdoubleArray weightAndBais = env->NewDoubleArray(size);
    env->SetDoubleArrayRegion(weightAndBais, jsize{0}, size, &ans[0]);
    return weightAndBais;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_turgo_NotificationSettingActivity_predictSetting(JNIEnv *env, jobject thiz,
                                                                  jdouble weight,
                                                                  jdouble bias,
                                                                  jdouble age) {
    double w = weight, b = bias, x = age, threshold = 0.5;

    // get prediction
    double z = x*w+b;

    // apply sigmoid
    double truncated_prediction = 1/(1+ exp(-z));

    if (truncated_prediction>=threshold) return 1; // would like to have notifications on
    else return 0; // would not like to receive notifications
}