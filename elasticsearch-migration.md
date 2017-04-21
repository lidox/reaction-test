# ElasticSearch Migration
## Workflow
1. Öffne [Notepad++](https://notepad-plus-plus.org) und kopiere den Inhalt rein
2. Entferne manuell vorne sowie hinten die eckigen Klammern
3. STRG + SHIFT + R --> Replace Tab auswählen --> 'Erweitert (\r,\n ... etc.) auswählen
4. Suchen nach
```
,{"name"
```
5. Ersetzen durch
```
\r\n POST reactiontest/user \r\n {"name"
```
6. Die erste Zeile anpassen:
## Mapping
````JSON
PUT reactiontest
{
  "mappings": {
    "scores": { 
      "_all":       { "enabled": false  }, 
      "properties": { 
        "games.datetime":  {
          "type":   "date", 
          "format": "yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd||epoch_millis"
        }
      }
    }
  }
}
````
## Beispiel JSON
Beipiel JSON Datei, die in das 'Kibana Format' transfomiert werden muss :
```JSON
[
   {
      "name":"Artur",
      "age":24,
      "gender":"Female",
      "games":[
         {
            "datetime":"2016-12-12 08:21:17.997",
            "type":"PreOperation",
            "times":[
               504
            ]
         },
         {
            "datetime":"2016-12-12 08:21:25.368",
            "type":"PreOperation",
            "times":[
               388
            ]
         },
         {
            "datetime":"2016-12-12 08:22:31.892",
            "type":"InOperation",
            "times":[
               329
            ]
         },
         {
            "datetime":"2016-12-12 08:22:41.109",
            "type":"InOperation",
            "times":[
               326
            ]
         },
         {
            "datetime":"2016-12-12 08:22:51.911",
            "type":"InOperation",
            "times":[
               325
            ]
         },
         {
            "datetime":"2016-12-12 08:23:01.086",
            "type":"InOperation",
            "times":[
               295
            ]
         },
         {
            "datetime":"2016-12-12 08:23:11.025",
            "type":"InOperation",
            "times":[
               300
            ]
         },
         {
            "datetime":"2016-12-13 14:07:58.043",
            "type":"PreOperation",
            "times":[
               338
            ]
         },
         {
            "datetime":"2017-03-01 12:55:35.666",
            "type":"PreOperation",
            "times":[
               327
            ]
         },
         {
            "datetime":"2017-03-01 12:55:45.645",
            "type":"PreOperation",
            "times":[
               391
            ]
         },
         {
            "datetime":"2017-03-02 14:56:55.713",
            "type":"PreOperation",
            "times":[
               418
            ]
         },
         {
            "datetime":"2017-03-02 14:57:01.461",
            "type":"PreOperation",
            "times":[
               367
            ]
         }
      ]
   },
   {
      "name":"Peter",
      "age":0,
      "gender":"Male",
      "games":[

      ]
   },
   {
      "name":"Andre",
      "age":49,
      "gender":"Male",
      "games":[
         {
            "datetime":"2017-01-24 14:51:50.026",
            "type":"PreOperation",
            "times":[
               603
            ]
         },
         {
            "datetime":"2017-01-24 14:51:59.860",
            "type":"PreOperation",
            "times":[
               347
            ]
         },
         {
            "datetime":"2017-01-24 14:52:09.473",
            "type":"PreOperation",
            "times":[
               564
            ]
         },
         {
            "datetime":"2017-01-24 14:52:15.832",
            "type":"PreOperation",
            "times":[
               464
            ]
         },
         {
            "datetime":"2017-01-24 14:52:22.244",
            "type":"PreOperation",
            "times":[
               4365
            ]
         },
         {
            "datetime":"2017-01-24 14:52:32.454",
            "type":"PreOperation",
            "times":[
               826
            ]
         },
         {
            "datetime":"2017-01-24 14:52:37.594",
            "type":"PreOperation",
            "times":[
               387
            ]
         },
         {
            "datetime":"2017-01-24 14:52:43.206",
            "type":"PreOperation",
            "times":[
               436
            ]
         },
         {
            "datetime":"2017-01-24 14:52:49.035",
            "type":"PreOperation",
            "times":[
               628
            ]
         },
         {
            "datetime":"2017-01-24 14:52:54.547",
            "type":"PreOperation",
            "times":[
               559
            ]
         }
      ]
   },
   {
      "name":"Jochen",
      "age":0,
      "gender":"Male",
      "games":[

      ]
   },
   {
      "name":"lera",
      "age":14,
      "gender":"Female",
      "games":[
         {
            "datetime":"2017-01-07 13:29:08.611",
            "type":"PreOperation",
            "times":[
               321
            ]
         },
         {
            "datetime":"2017-01-07 13:29:21.851",
            "type":"PreOperation",
            "times":[
               302
            ]
         },
         {
            "datetime":"2017-01-07 13:29:36.715",
            "type":"PreOperation",
            "times":[
               354
            ]
         },
         {
            "datetime":"2017-01-07 13:29:50.574",
            "type":"PreOperation",
            "times":[
               501
            ]
         },
         {
            "datetime":"2017-01-07 13:30:01.922",
            "type":"PreOperation",
            "times":[
               299
            ]
         },
         {
            "datetime":"2017-01-07 13:30:14.223",
            "type":"PreOperation",
            "times":[
               743
            ]
         },
         {
            "datetime":"2017-01-07 13:30:30.343",
            "type":"PreOperation",
            "times":[
               340
            ]
         }
      ]
   }
]
```
