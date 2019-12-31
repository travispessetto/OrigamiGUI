# Origami SMTP (Origami GUI)

[![Donate](https://liberapay.com/assets/widgets/donate.svg)](https://liberapay.com/travispessetto/donate)[![Gitter chat](https://badges.gitter.im/OrigamiSMTP/gitter.png)](https://gitter.im/OrigamiSMTP) [![Bountysource](https://img.shields.io/bountysource/team/origami-smtp/activity.svg)](https://www.bountysource.com/teams/origami-smtp) [![Github Releases](https://img.shields.io/github/downloads/travispessetto/OrigamiGUI/latest/total.svg)](https://travispessetto.github.io/OrigamiSMTP/#download) [![Build Status](https://travis-ci.org/travispessetto/OrigamiGUI.svg?branch=master)](https://travis-ci.org/travispessetto/OrigamiGUI)

Origami originally started as an attempt to make an SMTP server for development that could
handle TLS (secure) connections due to a project I was working on at work.  There was code
throughout the application making it impractical to just disable TLS and the fake SMTP servers
I tried either did not have the ability to use TLS or crashed thus Origami SMTP was born.

It started out and still is two separate projects. [Origami SMTP][1] was the console application
and library and Origami GUI (this project) was the graphical user interface for that library
in order to make it easier to use. I decided to call the combined work Origami SMTP.

This project, Origami GUI, is built around JavaFX and in the latest versions of Java you will
have to do some additional steps to get it to run.  Instructions will be provided for Netbeans
only at this time.

## Configuring Netbeans

After you have imported the project into Netbeans:

* Make sure you have downloaded the [OpenJFX Libraries][2] and installed them to a location you are comfortable with (I used C:\javafx-sdk-11.0.2)
* Right-click on the project name and the Properties in the menu
* Find run under Categories and then enter the arguments `--module-path=C:\javafx-sdk-11.0.2\lib --add-modules=javafx.controls,javafx.graphics,javafx.fxml,javafx.web,javafx.base`, but make sure to change this to your machine's path.  That should be it.

## Contributing

Monetary as well as code donations are greatly appreciated.  You can donate 
money via one of our Gitcheese or Bountysource badges.

Contributing code is easy.  Just fork the project and send us a pull 
request when you are done.

## Tips for Setting up your Environment

The `VERSION` file should match the tag at all times.  Changing this
value should align with creating a new version.  In order to make git 
automatically create a tag if that file changes paste the following code
in your `.git/post-commit` file.

```sh
#!/bin/sh

# Only show most recent tag without trailing commit information
git describe --tags | awk "{split(\$0,a,\"-\"); print a[1];}" > version.tmp

# Only proceed if version number has actually changed (i.e. a new tag has been created)
if [ ! $(cmp --silent version.tmp version.txt) ] 
then
    NEWVER=$(cat version.txt)
    echo Adding tag $NEWVER
    git tag -a $NEWVER -m ''
    rm version.tmp
fi
```

## How Donations are Spent

* Bounties
* Paying Developers
* Advertising

## License

[MIT License](license.txt)

[1]: https://github.com/travispessetto/origamismtp
[2]: https://openjfx.io/
