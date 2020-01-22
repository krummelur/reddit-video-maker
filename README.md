# reddit-video-maker
A silly little app that, given a specific subreddit will create a "reddit-video".
I kept seeing these videos being recommended on youtube. I don't like them but it seemed like the process could be easily automated.
The videos have a robot voice reading the posts, and some sweet royalty free bach music (which you can substitute for your own), with a screenshot of the post scrolling along with the robot reading.

Sadly you need to supply your own TTS implementation to use it, as I don't have the rights to share my solution.

uses ffmpeg for media processing and selenium with chromedriver for the screenshots.
