fetchUserTable("http://localhost:3000/customers")

document.getElementById("userDataForm").addEventListener("submit", (event) => {
    event.preventDefault()
    handleUserDataForm()
}) 

function handleUserDataForm(){
    const form = document.getElementById("userDataForm")
    const formData = new FormData(form)

    const urlBuilder = []

    const max = /^\.\.\d{1,4}$/
    const min = /\d{1,4}\.\./
    const range = /\d{1,4}\.\.\d{1,4}/

    for(const [name, valueWithSpace] of formData.entries()){
        let value = valueWithSpace.replace(/\s+/g, "").replace(/^0$/g,""); 
        if(value){
            value = value.replace(/"/g,"")
            value = value.replace(/^0001-01-01$/g,"")
            if(name === "_page"){
                urlBuilder.push(`${name}=${value}&_limit=25`)
            }
            else if(!value.includes("..")){
                urlBuilder.push(`${name}=${value}`)
            }
            else{
                if(range.test(value)){
                    const rangeString = value.split("..")
                    urlBuilder.push(`${name}_gte=${rangeString[0]}&${name}_lte=${rangeString[1]}`)
                }
                else if(max.test(value)){
                    const rangeString = value.split("..")
                    urlBuilder.push(`${name}_lte=${rangeString[1]}`)
                }
                else if(min.test(value)){
                    const rangeString = value.split("..")
                    urlBuilder.push(`${name}_gte=${rangeString[0]}`)
                }
            }
        }
    }
    const url = `http://localhost:3000/customers?${urlBuilder.join("&")}`
    console.log(url)
    fetchUserTable(url)
}

function fetchUserTable(url){
    fetch(url)
    .then(response => response.json())
    .then(data => {
        const customerList = Array.isArray(data) ? Array.from(data) : [data]
        loadUserTable(customerList)
    })
    .catch(error => console.error(error) );
}



function loadUserTable(customerList){
    const table = document.createElement("table")
    const firstLine = table.insertRow()
    const headers = [
        "ID", "Name", "Age", "Gender", "Postal code", "Email",
        "Phone number", "Membership status", "Join date", "Last purchased at",
        "Total spending", "Avg. order value", "Frequency", "Preferred category", "Churned"
    ];

    headers.forEach(header => firstLine.insertCell().textContent = header);
    firstLine.id = "sticky"

    try{
        for(const customerObj of customerList){
            const line = table.insertRow()
            line.insertCell().textContent = customerObj["id"]
            line.insertCell().textContent = customerObj["firstName"] + " " + customerObj["lastName"]
            line.insertCell().textContent = customerObj["age"]
            line.insertCell().textContent = customerObj["gender"]
            line.insertCell().textContent = customerObj["postalCode"]
            line.insertCell().textContent = customerObj["email"]
            line.insertCell().textContent = customerObj["phone"]
            line.insertCell().textContent = customerObj["membership"]
            line.insertCell().textContent = customerObj["joinedAt"]
            line.insertCell().textContent = customerObj["lastPurchaseAt"]
            line.insertCell().textContent = customerObj["totalSpending"]
            line.insertCell().textContent = customerObj["averageOrderValue"]
            line.insertCell().textContent = customerObj["frequency"]
            line.insertCell().textContent = customerObj["preferredCategory"]
            line.insertCell().textContent = customerObj["churned"]
        }
    }catch{
        table.insertRow().insertCell().textContent = "Error while fetching data"
    }
    const legend = document.getElementById("userLegend")
    legend.innerText = `Results: ${customerList.length}`
    const userTable = document.getElementById("userTable")
    userTable.innerHTML = ""
    userTable.appendChild(table)
}