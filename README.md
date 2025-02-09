# FetchQuest

An app designed to increase community involvement and social responsibility through an interactive quest system that allows for exciting volunteering opportunities in your community.

## Inspiration
There’s a growing global focus on social responsibility and helping those in need. But often, there’s a gap caused by issues with ease of access and lack of incentive. This inspired us to create a gamified solution to make mundane tasks more engaging. Our goal was to get more people involved by turning good deeds into an enjoyable experience. 

## What it does
The app first greets the user with a sign-in page, which authenticates with Google. Then the app displays a map of all their quests, a quest list for them to add more quests or remove quests, and a stats page to view their progress. Each quest is an opportunity to volunteer to help a local organization fulfill its mission. One interesting feature that we added was the streak tracker, which lights up if the user has participated in quests for at least three days in a row. This adds to the incentive since having a streak is proven to increase user interest.

## How we built it
We started the project by identifying our core purpose. With that in mind, we came up with a design using Figma for what we wanted our app to look like. We then set up a GitHub repository to help make collaboration easier and allow us all to work on it together. For the frontend we used Jetpack Compose for Android to create a fantasy, adventurer’s guild-looking user interface. The backend was made with Go, using SQLite to store user data. Users authenticate with Google and then their Google account is linked to an account record in our own database.

## Challenges we ran into
A challenge that we ran into was trying to get the Google sign-in to work. This required Google Play Services, which is essentially a black box, so it was very difficult to debug various undocumented errors in our sign-in flow. For example, we spent a while troubleshooting an error that said our Google Cloud developer console was not configured correctly but didn’t tell us what was incorrect about it. Implementing Google sign-in took us much longer than expected, but the added ease of use was worth it.

Also, while working on the UI, we found it pretty difficult to style it to our needs. We wanted the app to feel very different from your typical Material Design Android app, so we needed to push the limits of what was customizable with Jetpack Compose.

## Accomplishments that we're proud of
We have created an application that functions well and looks good. We are especially proud of trying to develop something that will allow for the improvement of communities. We are also very proud of our gamification of the app, allowing users to find the app fun and motivating, rather than as another chore, which is the reason for our app.

## What we learned
We learned to develop apps in a new language, Kotlin. Kotlin is part of the Java ecosystem, which means that the syntax stayed the same, even though we had to learn all the new methods. We also learned to use Go for the backend. Go was a challenge to learn, but we managed to incorporate it into our backend software for things like the Google sign-in and managing the map. One of the most important things we learned was making sure the app was targeted to our target audience. This app was designed for high schoolers, so we had to make sure that all the language we used resonated with them, as they would relate to the language we used. 

## What's next for FetchQuest
Our next step for FetchQuest is inviting organizations to add their quests to the app. This is to ensure that users will have many options to choose from, and can find something they enjoy and wish to continue volunteering. We also plan to add more features to encourage young adults to continue participating, like notifications for nearby quests.
