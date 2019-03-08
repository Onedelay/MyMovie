# ![](/files/boostcourse_ic.png)시네마천국

<p align="center">
	<img src="/files/boostcourse_workflow.png"></p>
<br>

## # 프로젝트 개요

### 부스트코스 에이스 1기 프로젝트

부스트코스에서 제공하는 서버로부터 영화 정보를 받아 보여주는 앱입니다.

단계별로 프로젝트가 진행되며, 각 프로젝트마다 전문가의 코드리뷰를 받고 Pass 를 받아야만 다음 단계로 진행할 수 있는 방식으로 진행되었습니다.

```
프로젝트1. 영화상세 화면 만들기
프로젝트2. 좋아요와 한줄평 리스트
프로젝트3. 한줄평 화면으로 전환하기
프로젝트4. 영화목록과 바로가기 메뉴
프로젝트5. 서버에서 영화정보 가져오기
프로젝트6. 영화정보를 단말에 저장하기
프로젝트7. 사진보기와 동영상 재생
프로젝트8. 영화 앱을 더 즐겁게
```

[리뷰어님들께 받은 조언들](#-리뷰어님들께-받은-조언들)

<br><br>

## # 프로젝트 소개

- 개인프로젝트
- 개발기간 : 2018.05~2018.09 (현재 리팩토링중)
- 개발언어 : Android(Java -> Kotlin[2019.03])
- 시연영상 : https://youtu.be/x1iFegux57Q
  <br><br>

## # 주요기능

- [**영화 목록 보기**](#1-영화-목록-보기) - 앱의 메인화면으로, 서버로부터 영화 목록 데이터를 받아 보여줍니다.
- [**영화 상세 보기**](#2-영화-상세-보기) - 서버로부터 영화 상세정보 데이터를 받아 보여줍니다.
- [**한줄평 보기**](#3-한줄평-보기) - 선택한 영화에 대한 한줄평을 작성할 수 있고, 작성된 한줄평을 모두 볼 수 있습니다.

<br><br>

## # SDK 버전 및 라이브러리

- minSdkVersion : 24
- targetSdkVersion : 27

```groovy
implementation 'com.android.support:appcompat-v7:27.1.1'
implementation 'com.android.support.constraint:constraint-layout:1.1.2'
implementation 'com.android.support:support-v4:27.1.1'
implementation 'com.android.support:design:27.1.1'
implementation 'com.android.support:recyclerview-v7:27.1.1'
implementation 'de.hdodenhof:circleimageview:2.2.0'
	
// REST API
implementation 'com.android.volley:volley:1.1.0'
implementation 'com.google.code.gson:gson:2.8.2'

// image loader
implementation 'com.github.bumptech.glide:glide:3.8.0'

// AAC
implementation "android.arch.persistence.room:runtime:${aac_version}"
implementation "android.arch.lifecycle:extensions:${aac_version}"
implementation "android.arch.lifecycle:livedata:${aac_version}"
kapt "android.arch.persistence.room:compiler:${aac_version}"
kapt "android.arch.lifecycle:compiler:${aac_version}"

// pinch zoom
implementation 'com.github.chrisbanes:PhotoView:2.1.4'

```

<br>

<br>

## # 개발한 기능 설명

### 1. 영화 목록 보기

<p align="center">
    <img src="/files/boostcourse_movie_list.png"></p>

앱의 메인화면으로, 서버로부터 영화 목록 데이터를 받아 화면에 보여줍니다. 인터넷이 연결되어있지 않은 경우에는 DB로부터 데이터를 불러옵니다. 액션바의 옵션메뉴를 통해 정렬 기준을 변경할 수 있습니다. 

- ViewModel클래스를 정의하여 DB 접근, 서버 요청 부분을 액티비티로부터 분리시켰습니다.

  - 인터넷이 연결되어있을 경우, 영화 목록을 DB에 저장하도록 ViewModel 클래스에 메서드를 구현하였습니다.

  - 영화 목록 정렬 옵션(예매순위, 큐레이션, 상영예정)에 따라 다르게 보여주도록 3개의 LiveData를 MediatorLiveData에 하나로 병합하여 관리하였습니다.

- ViewModel의 MediatorLiveData에 Observer를 구성하여 데이터 변경이 감지되면 화면이 갱신됩니다.

  ```kotlin
  private fun observeViewModel() {
          viewModel?.dataMerger?.observe(this, Observer { movieEntities ->
              movieEntities?.let { movies ->
                  adapter?.itemClear()
                  for (i in movies.indices) {
                      adapter?.addItem(PosterFragment.newInstance(i + 1, movies[i]))
                  }
              }
              viewPager.currentItem = viewModel?.curPosition ?: 0
          })
      }
  ```

- 영화 상세화면에서 백버튼을 누르거나 네비게이션바의 영화목록 버튼을 누르면, 이전에 보여주고있던 viewPager의 position을 유지하도록 프래그먼트의 백스택을 이용하였습니다.

  - 버그 발견(2019.03) : 영화 상세화면으로 이동할 경우, Movie 엔티티가 업데이트되어 onChanged 메서드가 호출되는 문제 발생 -> 상세 화면 이동 시 currentPosition을 저장하고, 되돌아왔을 때 position을 복구하는 방식으로 해결

- 영화 정렬 팝업 메뉴는 커스텀으로 구현하였고, translate 애니메이션이 적용되었습니다.

<br>

### 2. 영화 상세 보기

<p align="center">
    <img src="/files/boostcourse_movie_detail.png"></p>

서버로부터 영화 상세정보 데이터를 받아 화면에 보여줍니다. 해당 영화의 갤러리와, 가장 최근에 작성된 한줄평을 보여줍니다. (하단의 버튼은 동작하지 않습니다.) 인터넷이 연결되어있지 않은 경우에는 DB로부터 데이터를 로드합니다.

- 화면은 프래그먼트로 구성하였고, ConstraintLayout을 이용하여 레이아웃의 깊이를 줄였습니다.

- 영화 상세 프래그먼트로 교체되면, 인터넷 연결을 확인한 후 서버에 상세정보를 요청하여 DB에 저장합니다.

- 인터넷이 연결되지 않은 상태에서 DB 내용이 없을 경우 상세 화면을 띄워주지 않습니다. 

- 갤러리는 RecyclerView를 이용해 구성하였고, 사진을 누르면 핀치줌이 가능한 Activity가 띄워지고 동영상을 누르면 유튜브 동영상이 재생됩니다.

  - 핀치줌 기능은 라이브러리를 사용하였습니다.
  - GalleryItem에 멤버변수로 type을 두고, 동영상일 경우 뷰홀더에서 재생 버튼 이미지가 보이도록 하였습니다.
  - 갤러리에서 동영상의 썸네일은 다음과 같이 추출하였습니다.

  ```java
  private void setGalleryList(MovieEntity movie) {
          if (movie.getPhotos() != null && movie.getVideos() != null) {
              String[] photos = movie.getPhotos().split(",");
              String[] videos = movie.getVideos().split(",");
              ArrayList<GalleryItem> items = new ArrayList<>();
  
              for (String s : photos) {
                  items.add(new GalleryItem(s, Constants.GALLERY_TYPE_PHOTO, s));
              }
  
              for (String s : videos) {
                  String id = s.split("/")[3];
                  items.add(new GalleryItem("https://img.youtube.com/vi/" + id + "/0.jpg", Constants.GALLERY_TYPE_MOVIE, s));
              }
              adapter.addItems(items);
              adapter.notifyDataSetChanged();
          }
      }
  ```

<br>

### 3. 한줄평 보기

<p aline="center">
    <img src="/files/boostcourse_review.png"></p>

해당 영화에 대한 한줄평을 작성하고 서버에 저장된 한줄평 목록을 조회할 수 있습니다. 인터넷이 연결되어있지 않은 경우에는 DB로부터 데이터를 불러오고, 한줄평 작성과 추천을 할 수 없습니다. 

- RecyclerView를 이용해 구성하였습니다.

- ViewModel클래스를 정의하여 한줄평 DB 접근, 서버 요청 부분을 액티비티로부터 분리시켰습니다.

- startActivityForResult, onActivityResult를 이용하여 한줄평을 작성하였을 경우 서버 요청, 화면 갱신을 수행합니다.

- RecyclerView 어댑터의 데이터가 효율적으로 변경되도록, DiffUtil 클래스를 활용했습니다.

  ```kotlin
  /**
   * @param newList DB 로부터 새로 불러온 데이터
   */
  fun updateItem(newList: List<ReviewEntity>) {
          val callback = ListDiffCallback(this.items, newList)
          val diffResult = DiffUtil.calculateDiff(callback, false)
  
          this.items.clear()
          this.items.addAll(newList)
  
          diffResult.dispatchUpdatesTo(this@ReviewAdapter)
  }
  ```


<br>

### 4. 그 외

- 서버로부터 받은 응답 클래스는 제너릭를 이용해 정의하였습니다.

- 새로운 Request 클래스를 정의하여 json으로 파싱하고, 백그라운드에서 DB 작업을 수행할 수 있도록 parseNetworkResponse를 재정의하였습니다.

  ```java
  public class GsonRequest<T> extends Request<T> {
      private final Gson gson = new Gson();
      private final TypeToken<T> token;
      private final Response.Listener<T> listener;
      
      @WorkerThread
      @Override
      protected Response<T> parseNetworkResponse(NetworkResponse response) {
          ...
          listener.onResponse(result);
          ...
      }
  }
  ```


<br><br>



## # 리뷰어님들께 받은 조언들

- Left와 Right대신 Start, End로 바꾸면 왼쪽에서 오른쪽으로 쓰는 문화권과 오른쪽에서 왼쪽으로 쓰는 문화권 모두에 대응이 된다. (RTL)

- onCreate에서 코드를 모두 작성하는 것보다는 따로 메소드로 빼는 것이 좋다. 가독성을 높이고 유지보수를 쉽게 할 수 있다.

- Java code convention을 참고하면 좋다.

- 문자열 리소스의 경우 res/values/strings.xml 에 지정하여 참조하는 것이 좋다. 그렇게 하면 유지 보수 및 다국어 지원에 유리하다. 

- onActivityResult를 사용할 경우에는 requestCode와 resultCode를 사용해서 필요한 경우에만 내부 로직을 수행하도록 작성하는것이 좋다. 여러 requestCode를 사용하여 activity를 실행할 경우 예상치 않는 결과가 발생할 수 있다. 

- if 문 다음에 코드가 한줄만 있을 경우에 괄호를 어떻게 붙일지 자신만의 규칙을 만드는것이 도움이 된다. 

- bundle안에 값을 넣을때 사용되는 key는 문자열 상수로 지정하여 사용하는것이 좋다. 문자열 상수를 사용하면 값을 넣어주는 곳과 값을 꺼내는 곳에서 오타를 줄일 수 있다. 

- RequestQueue를 각 activity에서 생성하지 않고 Application에서 생성하는것이 좋다. <https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class> 링크의 내용을 보면 Application을 상속받아 앱의 Application 객체를 만들 수 있고 onCreate() 메소드 안에 requestQueue를 생성하는 코드를 넣어주면 앱의 생명주기에 맞게 RequestQueue를 생성하고 사용할 수 있다. 

- 큰 문제는 아니지만, activity 패키지 안에 여러 액티비티가 들어가는 것을 의미한다면, activities 처럼 복수형태로 적어주는게 더 일반적이다. 

- 토스트 메세지도 resources의 strings.xml에 정의하여 사용하는 것이 더 좋다.

- Adapter 내에 선언된 ViewHolder 클래스에 static을 붙인 이유에 대해 생각해보는 것이 좋다.

  > 그래서 찾아본 링크 : https://stackoverflow.com/questions/31302341/what-difference-between-static-and-non-static-viewholder-in-recyclerview-adapter

- DiffUtil 관련 조언

  > DiffUtil은 구글이 밝히길, 내부적으로 Eugene W. Myers가 제안한 diff algorithm을 사용합니다. 이 알고리즘은 공간에 최적화 되어있어 아이템이 N개 있을 때, 공간복잡도는 O(N)이다.
  >
  > 대신 시간복잡도는 일반적인 경우 old/new 두 리스트의 합인 N개의 아이템과, old가 new로 변환되기 위해 필요한 최소 작업갯수D가 있을 때 O(N + D^2)입니다.
  >
  > 게다가 아이템 이동이 있는 경우 Myers의 알고리즘 이후 2nd pass를 타는데, 추가되거나 삭제된 아이템의 총 갯수를 N’라 할 때 O(N’^2)만큼의 시간을 더 사용하게 됩니다.
  >
  > 시간복잡도가 꽤 높은 알고리즘이므로 대량의 데이터를 다룰 경우 worker thread에서 수행을 하고, 전체 아이템 갯수와 이동/변경되는 아이템 갯수가 많으면 수행시간이 상당히 커지게됩니다.
  >
  > 그러니 테스트를 해보고 수행하는것을 권장드립니다.
