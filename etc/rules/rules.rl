@prefix scutter: <http://purl.org/net/scutter/>.
@prefix lm: <http://jena.hpl.hp.com/2004/08/location-mapping#> .

[inferLocationMappingFromScutter:

   (?R rdf:type scutter:Representation), 
     (?R scutter:source ?source),
      (?R scutter:localCopy ?local),
         makeTemp(?mapping)  
  
     -> (?mapping lm:name ?source),
        (?mapping lm:altName ?local)]

[connect:
   (?mapping lm:name ?source), makeTemp(?x)
      -> (?x lm:mapping ?mapping)]