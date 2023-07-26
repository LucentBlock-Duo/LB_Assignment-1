# LB_Assignment-2

# 차량 정비 예약 API Server

# 기능.
* 회원가입 / 로그인을 통한 회원 관리 (OAuth2 Supported)
* 차량 정보 등록
* 차량 정비 예약


# Architecture
* Java 17, Spring 3.1.1
* MySQL 8.0.33
* Docker
* Jenkins
* GKE

# Setup

## Prerequisite
* Docker In Computing Engine (VM)
* Google Cloud (GKE, Computing Engine)
* Set Access Authorization In Computing Engine
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/d401a85a-e42c-4c3c-8090-e5420f7ac3e9)


## Step 1. Run Docker Container in computing engine
```
sudo docker run -d -p 8080:8080 --name jenkins -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker -v /jenkins:/var/jenkins_home rkddlfah02/lb-jenkins
```

## Step 2. Setup gcloud CLI, Kubectl in Jenkins Container
**Connect to Jenkins Container**
```
sudo docker exec -it jenkins bash
```

**Install gcloud CLI by following below page**
https://cloud.google.com/sdk/docs/install?hl=ko#deb

**Install kubectl in jenkins container**
```
sudo apt-get install kubectl
```
**Init gcloud cli in jenkins container**
follow below page
https://cloud.google.com/sdk/docs/initializing?hl=ko

**Intsall gke-gcloud-auth-plugin in jenkins container**
```
sudo apt-get install google-cloud-sdk-gke-gcloud-auth-plugin
```

## Step 3. Connect to GKE
**1. Click Connect Button**
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/8ba594bf-8c3a-4e78-ae88-310e0745a574)

**2. Copy And Paste to Jenkins Container**
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/5f3039db-3d0d-45d0-b686-4b172dc3418b)

## Step 4. Setup Jenkins Pipeline
You can obtain the administrator password from the result of the command below.
```
sudo docker logs jenkins
```
Paste the password.
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/eeb33b4d-a3a8-4bed-8a66-77e27e74921a)

Configure github server settings with secret text
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/8448adbc-2beb-4c96-bf92-bc5ef76b6a05)

Make Item
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/d027b513-c57f-4a3a-8a95-2186d112fb49)

Select options
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/74ad6640-4b65-4eec-b7fd-c147945373e9)

Add Credential
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/a6a79fb3-0fcb-4c16-ad70-3a4359ae1755)

Configure pipeline settings
![image](https://github.com/LucentBlock-Duo/LB_Assignment-1/assets/96767857/ffe8ceca-85e0-49c4-b9c1-a3f2f85be2ca)


Then Jenkins do CI/CD following script (Jenkinsfile)

## 패치 노트 23.07.21.

**1. 지점 별 위치설정 기능 고도화**
* 정비소가 도로명 주소, 우편번호, 시, 군/구, 상세 주소, 위도, 경도 값을 가집니다.
* MySQL 5.7+ Spatial 연산(위경도 활용)을 활용한 가까운 정비소 찾기 지원

**2. 예약 후 관리 기능 추가**
* 수리공에 별점을 남길 수 있습니다.
* 예약 내역을 관리할 수 있습니다.
  * 어떤 정비공에게 예약을 받았는지 조회 가능
  * 로그인 한 사용자 기준으로 지금까지의 예약 내역 조회 가능
  * 자동차를 기준으로 예약 내역 조회 가능
  * 정비항목을 기준으로 예약 내역 조회 가능

**3. 정비공 X 정비항목 별 가격 제공**
   * 정비공 X 정비항목 별 다른 가격이 제공됩니다.
   * 예약 확정 전 결제 예상 금액이 표시됩니다.
  
**4. 사용자/정비공 예약 선호도 추가**
   * 사용자 / 정비공은 본인이 선호하는 차량 제조사/정비 항목을 등록할 수 있습니다.
   * 공통 선호도 개수를 기반으로, 추천 정비공 목록이 제공됩니다.

**5. 예약 전 검색 기능 추가**
   * 가까운 지점 / 별점이 높은 정비공 / 가격 / 예약 희망 일자 순으로 복합 검색이 가능해집니다. (Filtering)
   * 정렬 우선 순위는 가까운 지점 > 별점이 높은 정비공 > 낮은 가격 순으로 정렬됩니다.
  
  
