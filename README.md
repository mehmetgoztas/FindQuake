# FindQuake:Android App for Finding Missing Individuals After Earthquakes Using Facial Recognition with ML/Deep Learning/Cloud
# Demo
If you want to see how the project works, you can watch the [FindQukae Video](https://youtu.be/lpYXLx1vRGM).

## Introduction

Finding missing individuals in the aftermath of an earthquake poses significant challenges for concerned family members. Despite advancements in technology, people still rely on outdated methods to locate their loved ones during such critical situations. This thesis project aims to address this pressing issue by developing an innovative solution that harnesses the power of deep learning and cloud systems, specifically employing face recognition techniques. 

## Key Features

- **Face Recognition:** Our solution uses cutting-edge face recognition powered by machine learning models to accurately identify and match individuals, providing a reliable means of locating loved ones amidst the chaos following an earthquake.
- **Real-time Processing:** Integration of ML Kit and TensorFlow Lite ensures real-time face recognition capabilities on Android devices, optimizing performance and ensuring swift results.
- **Cloud Integration:** Utilization of cloud systems such as HuaweiCloud Firebase enables seamless data storage, synchronization, and sharing. Relevant information, including photos, descriptions, and last known locations, can be readily accessible to concerned families.

## Goals

The primary goal of this project is to contribute to the field of disaster response by bridging the gap between outdated methods and the potential of emerging technologies. The development of an Android application that combines face recognition, deep learning, and cloud systems has the potential to revolutionize the process of finding missing individuals after earthquakes. By providing a faster and more efficient solution, we strive to alleviate the anguish experienced by families in times of crisis and enable a swifter reunification process, bringing much-needed relief and peace of mind to those affected by such devastating events.

## Implementation

This README provides a step-by-step guide for implementing a project utilizing Android Studio, Firebase, Huawei Cloud, ML Kit, TensorFlow Lite, and MobileNet-FaceNet for face detection and recognition.

## Setting Up the Project

1. Install Android Studio and choose Java as the programming language.
2. Set the minimum SDK to 24 and the target SDK to 33 for compatibility and feature utilization.

### Permissions

Add the following permissions to your AndroidManifest.xml to enable required features:

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

## Firebase Integration

1. Create a Firebase project and enable Firebase Authentication.
2. Configure your Android Studio project by registering the app and adding the `google-services.json` file.
3. Add Firebase Authentication dependency to your app's `build.gradle`:

implementation 'com.google.firebase:firebase-auth:21.0.1'

4. Apply the Google Services plugin in your app's `build.gradle`.

### Firebase Authentication Implementation

Utilize Firebase Authentication for user management, including registration, login, and password reset. Refer to Firebase Authentication documentation for detailed instructions.

## Huawei Cloud Setup

1. Create a Huawei Cloud account and enable the OBS service.
2. Obtain the Access Key ID and Secret Access Key for authenticating your app with Huawei OBS.
3. Add Huawei OBS dependency to your app's `build.gradle`:

implementation 'com.huawei.hms:opendevice:1.0.1.300'

4. Initialize the Huawei OBS client with authentication credentials.

### Uploading and Downloading Objects from Huawei OBS

Implement functionalities to upload, download, and manage objects within Huawei OBS using the provided SDKs. Ensure secure storage of authentication credentials.

### Sending Emails using Huawei Cloud Mail

1. Create a Function Graph on Huawei Cloud for sending emails.
2. Utilize the Huawei Cloud SMN service within the Function Graph to send emails.

## Face Detection with ML Kit

1. Add ML Kit dependency to your app's `build.gradle`:

implementation 'com.google.android.gms:play-services-mlkit-face-detection:16.4.0'

2. Configure the face detector and integrate it into your app.

## Face Recognition with TensorFlow Lite and MobileNet-FaceNet

1. Add TensorFlow Lite dependency to your app's `build.gradle`:

implementation 'org.tensorflow:tensorflow-lite:3.0.0'

2. Convert the MobileNet-FaceNet model to TensorFlow Lite format.
3. Implement the face recognition logic by loading and processing the TensorFlow Lite model.

# TEST AND RESULTS

In this chapter, I will conduct two different tests: 1. Facial recognition test and 2. Comparison test of two different mobile devices.

## 5.1. Face Recognition Comparison and Accuracy Test

As shown in the table, 10 different photos of 5 different users were taken, and the facial recognition rates were compared using FaceNet, MobileNet, and my personal dataset. FaceNet accuracy results are shown in Fig. A.1, A.2, A.3, A.4, A.5, and MobileNet accuracy results are shown in Fig. A.6, A.7, A.8, A.9, A.10 with 10 different photos of 5 people with a total of 50 different photos.

According to the comparison results, FaceNet performed the best in terms of facial recognition rates, followed by MobileNet in the middle, and the personal dataset yielded the worst results.

Considering that the facial recognition system will be used for individuals lost in earthquakes, I chose FaceNet as it offers speed and the highest accuracy rates.

**Speed:** In earthquake situations, it is crucial to quickly identify the missing individuals. FaceNet can perform facial recognition processes swiftly and provide instant results.

**High Accuracy Rates:** The facial recognition system used to locate missing individuals must be reliable and effective. FaceNet is renowned for its high accuracy rates and its ability to make accurate matches.

**Dataset Selection:** Comparative analysis showed that FaceNet had the highest rates. Therefore, I used FaceNet’s own dataset to obtain the most reliable results. This dataset represents a broader set of data and includes photos of individuals from various age groups and genders.

In conclusion, by choosing FaceNet, I aimed to create a facial recognition system that is fast and achieves the highest accuracy rates. This solution aims to effectively and reliably identify individuals lost in earthquakes.

## 5.2. Real-Time Test

The Face Recognition App, named FindQuake, based on Android, will undergo testing in two areas: real-time tests and subjective tests. In all conducted tests, the application will be downloaded onto the Android phones of volunteers. An overview of the application’s general structure will be provided, highlighting the purpose of its development. Users will be allotted a specific duration to familiarize themselves with the application and gain an understanding of its functionality.

For real-time testing, four important scenarios were designed, with two volunteers simultaneously conducting the tests. The features of the phones used in the testing are presented in Table 5.1. Below, you can find the details of all the scenarios attempted, along with the application’s response and the corresponding images.

**Scenario 1:** Can you able to open the app at the same time? In the test conducted with two users at the same time, it has been shown that the users can use the application at the same time.

**Scenario 2:** When one of you registered, did the other of you log into the system with the email she registered with?

**Scenario 3:** When one of you registered a face in the system, was the face recognized in the data uploaded by the other user?

**Scenario 4:** When one of you recorded a face on the security camera, was the face recognized on the other’s camera?

## 5.2.1. Subjective Tests

SUS was released in 1986 by John Brooke. Many services and products such as websites, software, hardware, and mobile devices can be evaluated via this tool. SUS testing tool is preferred since it ensures reliability and efficiency for the usability measurements in the researches. In other words, the usability perception of the users can be quantified and measured by this tool ANOVA is the primary test for social sciences, medicine, biology, and considerably used in these fields for statistical analysis. Moreover, a variable which is whether continuous dependent or categorical independent can be tested statistically with the ANOVA testing. The test checks for the data in terms of consistency with a null hypothesis. Ten volunteers are found to test the application. The opinions of the users are taken by using the application on their phones. The test consists of ten questions and five options. Options are Strongly Disagree, Disagree, Neutral, Agree, and Strongly Agree.

1. I think the reader interface was very easy to use.
2. I think the listener interface is suitable for someone who is visually impaired.
3. I think I need support to use the application.
4. I think I like to listen to the books from the application in my daily life.
5. I think I would like to contribute to vocalizing the books regularly.
6. I think the page to be read is not clear enough to be able to read.
7. I think it was very easy to vocalize the books.
8. I think the application responds fast enough to my requests.
9. I liked the application in general.
10. I think the application meets its purpose.

In order for the data to be used, each option must be converted from one to five points. Accordingly, Strongly Disagree corresponds to 1 point, Disagree 2 points, Neutral 3 points, Agree 4 points, and Strongly Agree 5 points. Then, five is subtracted from the sum of the questions belonging to the odd number. The sum of the questions with an even number is subtracted from twenty-five. The two values are added up and multiplied by 2.5. Thus, the total score for the questions becomes one hundred. As a result of the calculations, the total score is calculated as 91.2. Comparing the total score with the result adjective rating is Excellent. The grade is A. General score table for the questions are generated in Table 5.3.

| Question | Strongly Disagree | Disagree | Neutral | Agree | Strongly Agree |
| -------- | ----------------- | -------- | ------- | ----- | -------------- |
| Q1       | 0                 | 0        | 8       | 0     | 0              |
| Q2       | 0                 | 0        | 2       | 2     | 0              |
| Q3       | 8                 | 2        | 0       | 0     | 0              |
| Q4       | 0                 | 2        | 5       | 0     | 0              |
| Q5       | 0                 | 2        | 1       | 0     | 4              |


In conclusion, this thesis project presents a solution that utilizes deep learning and cloud systems to address the challenges faced in finding missing individuals following an earthquake. By incorporating face recognition techniques powered by machine learning models, the proposed solution aims to enhance the speed and accuracy of reuniting families. The integration of ML Kit and TensorFlow Lite enables real-time face recognition on Android devices, optimizing performance and ensuring swift results. Additionally, the utilization of cloud systems such as HuaweiCloud Firebase facilitates seamless data storage, synchronization, and sharing, supporting the efficient coordination of efforts during the reunification process.

This project contributes to the field of disaster response by bridging the gap between outdated methods and emerging technologies. By developing an Android application that combines face recognition, deep learning, and cloud systems, the solution has the potential to revolutionize the process of finding missing individuals after earthquakes. The implementation of this solution can alleviate the anguish experienced by families and provide a faster and more efficient means of reunification, bringing relief and peace of mind to those affected by such devastating events.

Moving forward, further research and development in this area can lead to even more advanced solutions that leverage the power of artificial intelligence and cloud systems. By continually improving and expanding the capabilities of such technologies, we can enhance disaster response efforts and improve outcomes for families in times of crisis. Ultimately, the goal is to provide a reliable and effective means of locating missing individuals, ensuring their safety and reuniting them with their loved ones as quickly as possible.

## 6.1. Future Work

To improve the accuracy of my own dataset, I aim to develop it further in order to achieve higher accuracy rates. I intend to integrate the email sending service with the police system, enabling automatic email notifications to be sent to the police when a person is located, facilitating communication with the relatives of earthquake victims. As cloud systems are limited by cost, I would like to utilize a more professional system to overcome these limitations. Additionally, I desire to integrate the security camera system to work synchronously with the cameras in hospitals.

## How to Contribute

Contributions to this project are welcome! If you're interested in helping to improve this solution or have suggestions, feel free to open an issue or submit a pull request.


