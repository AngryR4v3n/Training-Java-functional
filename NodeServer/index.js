
const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = 3000
const fs = require('fs')
let exists = null
const file = './db.json';
// parse application/json
const jsonParser = bodyParser.json()


// check if file exists. If it doesnt, create it
try{
    
    if(fs.existsSync(file)){
        exists = true
    } else {
        exists = false
    }
} catch(err){
    console.error(err)
    console.error('Something wrong with fs')
}


app.get('/', (req, res) => {
  res.status(200).send({'text':'Hello from ExpressJS!'})
})

app.post('/airport/add', jsonParser ,(req, res) => {
     console.log(req.body);
     res.status(200).send({'text':'Success, added the airport :)'})

     if(exists){
        fs.appendFile(file, `${req.body.iata}: ${JSON.stringify(req.body)},\n`, (err) => {err ? console.error(err) : null })
     } else{
        fs.writeFile(file, `{\n ${JSON.stringify(req.body.iata)}: ${JSON.stringify(req.body)},\n`, (err) => {err ? console.error(err) : null })
     }
})

app.get('/airport/close', jsonParser, (req, res) => {
    if(exists){
        //this is lazy as I should remove the last comma, but this can be done manually I guess >:)
        fs.appendFile(file, '}', (err) => {err ? console.error(err) : null})
     } else{
        res.status(200).send({"text": "Nothing to close ;)"})
     }
})

app.listen(port, () => {
  console.log(`Simple server listening on port ${port}`)
})
