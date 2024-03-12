# Directory Tree Structure

```
├── .github
│   ├── workflows
│       └── update_readme.yml
└── HELP.md
└── README.md
└── build.gradle
├── gradle
│   ├── wrapper
│       └── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── gradlew
└── gradlew.bat
└── settings.gradle
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── HomePage
│   │   │       ├── DB
│   │   │       │   └── MySqlTest.java
│   │   │       └── WebSiteApplication.java
│   │   │       ├── config
│   │   │       │   └── BCryptPasswordConfig.java
│   │   │       │   └── CorsConfig.java
│   │   │       │   └── FilterConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       │   └── SpringConfig.java
│   │   │       │   ├── auth
│   │   │       │   │   └── PrincipalDetails.java
│   │   │       │   │   └── PrincipalDetailsService.java
│   │   │       │   ├── jwt
│   │   │       │   │   └── JwtAuthenticationFilter.java
│   │   │       │   │   └── JwtAuthorizationFilter.java
│   │   │       │   ├── oauth
│   │   │       │       └── PrincipalOauth2UserService.java
│   │   │       │       ├── provider
│   │   │       │           └── FacebookUserInfo.java
│   │   │       │           └── GoogleUserInfo.java
│   │   │       │           └── NaverUserInfo.java
│   │   │       │           └── OAuth2UserInfo.java
│   │   │       ├── controller
│   │   │       │   └── AuthenticationController.java
│   │   │       │   └── IndexController.java
│   │   │       │   └── RestApiController.java
│   │   │       │   └── UserController.java
│   │   │       │   └── UserForm.java
│   │   │       ├── domain
│   │   │       │   ├── model
│   │   │       │       └── User.java
│   │   │       ├── filter
│   │   │       │   └── Filter1.java
│   │   │       │   └── Filter2.java
│   │   │       │   └── Filter3.java
│   │   │       ├── repository
│   │   │       │   └── JdbcTemplateUserRepository.java
│   │   │       │   └── MemoryUserRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── service
│   │   │           └── UserService.java
│   │   ├── resources
│   │       └── application.properties
│   │       ├── static
│   │       │   ├── css
│   │       │   │   └── bootstrap.css
│   │       │   │   └── responsive.css
│   │       │   │   └── style.css
│   │       │   │   └── style.css.map
│   │       │   │   └── style.scss
│   │       │   ├── fonts
│   │       │   │   └── AmericanTypewriterBQ-Bold.otf
│   │       │   ├── images
│   │       │   │   └── about-img.jpg
│   │       │   │   └── app-bg.jpg
│   │       │   │   └── app-store.png
│   │       │   │   └── call.png
│   │       │   │   └── client.png
│   │       │   │   └── dish.jpg
│   │       │   │   └── envelope.png
│   │       │   │   └── facebook.png
│   │       │   │   └── fb.png
│   │       │   │   └── google.png
│   │       │   │   └── hot-1.png
│   │       │   │   └── hot-2.png
│   │       │   │   └── hot-3.png
│   │       │   │   └── left-angle.png
│   │       │   │   └── linkedin.png
│   │       │   │   └── location.png
│   │       │   │   └── logo.png
│   │       │   │   └── man-with-phone.png
│   │       │   │   └── naver.png
│   │       │   │   └── next-black.png
│   │       │   │   └── next-grey.png
│   │       │   │   └── play-store.png
│   │       │   │   └── prev-black.png
│   │       │   │   └── prev-grey.png
│   │       │   │   └── right-angle.png
│   │       │   │   └── slider-img.png
│   │       │   │   └── twitter.png
│   │       │   │   └── youtube.png
│   │       │   ├── js
│   │       │       └── bootstrap.js
│   │       │       └── jquery-3.4.1.min.js
│   │       ├── templates
│   │           └── about.html
│   │           └── contact.html
│   │           └── food.html
│   │           └── home.html
│   │           └── index.html
│   │           ├── login
│   │           │   └── loginForm.html
│   │           ├── user
│   │               └── createUserForm.html
│   │               └── userList.html
│   ├── test
│       ├── java
│           ├── HomePage
│               └── WebSiteApplicationTests.java
│               ├── repository
│               │   └── MemoryUserRepositoryTest.java
│               ├── service
│                   └── UserServiceIntegrationTest.java
│                   └── UserServiceTest.java
└── update_readme.py
```
