# Reaction Test App

## Research Goals
1. Compare pre- and in-operation reaction times to determine significance.
2. Determine wakefulness of the patient.
3. How quickly the patient is expected to be awake.
4. Find out the optimal time period of wakefulness.

## Abstract
In order to remove brain tumors or metastases awake surgeries are performed in the Clinic for
Neurosurgery in Düsseldorf. During the awake surgery, the patient is locally anesthetized,
but available. The surgeon can communicate with the patient to check whether the speech
center is still functioning.

At the moment, the optimal period of the patient’s degree of attention is not sufficiently
investigated for surgeries. However, this period is best suited for awake surgeries and is of
immense importance for neurosurgery.

The novel mobile application (App) presented in this thesis is used to investigate the optimal
period of degree of attention using statistical techniques. Therefore, the patient’s reaction
time (RT) is measured during the entire clinical treatment using the introduced App. The
patient’s degree of attention is derived from the RT. In addition, during the operation, the
surgical team benefits by getting notified in real time as soon as the degree of attention is
significantly worse than expected.

Statistical techniques are performed to predict RTs and to detect outliers. In the evaluation,
these techniques are examined using quality factors, such as aesthetics, engagement, App
functionality, outlier detection functionality as well as forecasting functionality.

In conclusion, the patient’s degree of attention is accurately identified and the surgeon is notified
when the degree of attention is significantly worse than expected. The outlier detection
algorithm has minimal weaknesses which can be eliminated by means of optimizations presented.
Due to the basic conditions of awake surgeries, RT sessions can not be carried out at
any time. But irregular sessions have negative impact to the quality of the forecast. Thus two
alternatives are proposed which can overcome this issue in the future.

## Documents
* Take a look on the [Slides](https://docs.google.com/presentation/d/1j-WWPEJoS2XAGXu7pDNfqmuJlSc0gUZMXScSQgE4h-g/edit?usp=sharing)
* Download the [master thesis (PDF)](https://github.com/lidox/reaction-test/files/1490679/master-thesis-artur-schaefer.pdf)

## Download APK
[Reaction Test APK (release 1.0.5)](https://github.com/lidox/reaction-test/files/1490733/app-release-1-0-5.zip)

## UI Demo
UI           |  Demo
:-------------------------:|:-------------------------:
![5_result_screen](https://user-images.githubusercontent.com/7879175/33061014-b445d326-ce9a-11e7-831b-5ea03db52644.png)  | ![6_operation_mode](https://user-images.githubusercontent.com/7879175/33061015-b45ce6ce-ce9a-11e7-9c06-52937bb5af71.png)
![18_medical_assistent_survey](https://user-images.githubusercontent.com/7879175/33061007-b34a6ec8-ce9a-11e7-9b8a-4caa975f6466.png)  | ![17_game_1](https://user-images.githubusercontent.com/7879175/33061006-b334bccc-ce9a-11e7-8e87-dd0bce7bc85c.png)
![1_start_menu](https://user-images.githubusercontent.com/7879175/33061010-b39e3f9e-ce9a-11e7-8bc8-f559cb7a1bc0.png) | ![2_side_menu](https://user-images.githubusercontent.com/7879175/33061011-b3b3b914-ce9a-11e7-806e-363afb489d1a.png)
![8_operation_events](https://user-images.githubusercontent.com/7879175/33060997-b1be7644-ce9a-11e7-84fb-0aceb3243edb.png)  |  ![9_audio_messages](https://user-images.githubusercontent.com/7879175/33060998-b1f16ae0-ce9a-11e7-8729-36bac2d64dc5.png)
![10_event_notes](https://user-images.githubusercontent.com/7879175/33060999-b2229688-ce9a-11e7-9d76-1c258283b641.png)  |  ![11_operation_timeline](https://user-images.githubusercontent.com/7879175/33061000-b2738e1c-ce9a-11e7-87a5-880975886e06.png)
![12_medicament_list](https://user-images.githubusercontent.com/7879175/33061001-b28d528e-ce9a-11e7-9712-5a8eaee72097.png)  | ![13_new_medicament](https://user-images.githubusercontent.com/7879175/33061002-b2cc4f34-ce9a-11e7-9ed6-a8cd0b2cf4bc.png)
![16_settings](https://user-images.githubusercontent.com/7879175/33061005-b3219a52-ce9a-11e7-82f7-750dbeba2688.png) |  ![19_share_function](https://user-images.githubusercontent.com/7879175/33061008-b3609ae0-ce9a-11e7-9043-3593a23e2183.png)
![3_edit_operaiton_issue_name](https://user-images.githubusercontent.com/7879175/33061012-b3c9b322-ce9a-11e7-994c-0c81da6bee3d.png)  | ![4_add_new_user](https://user-images.githubusercontent.com/7879175/33061013-b4153a22-ce9a-11e7-8196-b0bbdbb5409b.png)
![7_operation_alarm](https://user-images.githubusercontent.com/7879175/33061016-b479032c-ce9a-11e7-8afe-32ba689b43bf.png)  |  ![](https://...Dark.png)
![](https://...Ocean.png)  |  ![](https://...Dark.png)

## Devices using the App (Developer Infos)
Use Samsung Galaxy S2 Tab Emulator for tests (1536 x 2048 pixels, 9.7 inches)

## Bayesian Statistics Ideas
In order to get the most of the data, Bayesian Statistics could make the difference.

[See slides...](https://docs.google.com/presentation/d/1tsnQKsVxss43J_OfOW4NiWoZHTViUGNHIo439mVVi5M/edit?usp=sharing) ツ
