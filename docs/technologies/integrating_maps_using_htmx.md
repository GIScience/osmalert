# Research about integrating existing map interaction libraries using HTMX.

https://jira-se.ifi.uni-heidelberg.de/browse/ISE23-87

Possible candidates: [Leaflet](https://leafletjs.com
 "Leaflet"), [OpenLayers](https://openlayers.org/ "OpenLayers")

Here a few notes about our tests with both:

## Leaflet: 
We researched in the web about existent solutions and found in particular two interesting projects, both based on leaflet:
+  [hyperleaflet](https://hyperleaflet.dev/ "Hyperleaflet")
is a library that allows you to use the popular Leaflet library with just HTML attributes. With Hyperleaflet, you can interact with Leaflet maps without writing a single line of JavaScript. It offers a wide range of capabilities by wrapping Leaflet's most useful features in an opinionated yet versatile way."

+  [hl-leaflet](https://socket.dev/npm/package/hl-leaflet/overview/0.0.3 "hl-leaflet" )
is maintained from only a person. The use is free for opensource projects

The try from the team with this two tools was until now unsuccessful. 

## OpenLayers: 
The testing seems to work. There are not many functionalities, like clearing the bounding box. 
Another problem is that not too much htmx is involved.
We are actual working on it.









