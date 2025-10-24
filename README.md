# Hiking App with Nicla Sense Me, Android studio
This is a repository of Android side of Hikking app

## Android Studio, installation
Downaload and then in ``Android Studio``: ``New``->``import Project``->``Select the downloaded file``.

## Phone installation
First finnish ``Android studio, installation`` and ``Google API key for Map``, then in Android Studio:
  1. ``Build``->``Generate App Bundles or APKs`` ->``Generate APKs``,
  2. After building, a notification should appear in ``Android studio`` and click ``locate`` on the notification,
  3. There you will se ``apk file``,
  4. Install it on ``Android phone``.
## Google API key for Map
To show map in application follow these steps:

1. Get API key for ``Maps SDK``. To do so follow [this documentation](https://developers.google.com/maps/get-started),
2. When you have an API key. Create ``secrets.properties`` in the same folder as ``locals.properties``,
3. In ``secrects.properties`` file add ``MAPS_API_KEY=<your api key>``.

Now you should have access to the map
