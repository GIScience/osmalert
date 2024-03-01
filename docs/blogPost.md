# OSMAlert - A web application for notifications about OSM changes

## Introduction

- why do we need it
- The goal

how project come to a being

## How it works

The application provides a website where jobs can be created to receive
notifications about OSM changes in a specific area.
The user has to give a unique job name, a valid e-mail address and coordinates
for a bounding box to submit the job. It is also possible to choose the time
interval in which the e-mails are sent. There are three options in the
time interval selection (minutes, hours, days).\
Another optional specification is the filtering for tags. There can be used any
key and value combination that is a valid tag in OSM.

![](images/website.png)

After the submission of a job the application continuously calculates the
average of changes
in the given area based on historical data from the ohsome API and new incoming
changes.
Then the standard deviation is used to detect an unusual high amount of changes
and the user will be informed about it within the e-mail.\
The notification also contains the number of changes in the given time interval,
how many users changed something and a note if there were problems with
receiving the historical data for the calculation. An example of an e-mail can
be seen below.

![](images/e-mail.png)

## Technical foundations

OSM planet server\
data flow overview

[](architecture/Simple_data_flow.puml)

## Conclusion

future work: vandalism detection