google.charts.load('current', {'packages':['corechart']})
google.charts.setOnLoadCallback(loadCharts)

const datas = {}
const urls = ["/customers/count","/customers/avg-age","/customers/most-frequent-purchase-category","/customers/sum-of-purchase","/customers/avg-order-value","/customers/purchase-frequency","/customers/gender-dist","/customers/membership-dist","/customers/categories","/customers/top-spenders","/customers/trends",]
run()

window.addEventListener('resize', function() {
    loadCharts();
});


async function run() {
    await fetchAllDataParallel()
    loadOverwievCharts()
    loadCharts()
    loadTable()
}

async function fetchDataFromApi(url, obj) {
    const response = await fetch(url)
    if(response.ok){
        const data = await response.json()
        Object.assign(obj, data)
    }else{
        const error = new Object()
        Object.assign(obj,error)
    }
}
async function fetchAllDataParallel() {
    await Promise.all(urls.map(url => fetchDataFromApi(url, datas)));
}

function loadOverwievCharts(){
    document.getElementById("count").innerText = datas["count"]
    document.getElementById("avg-age").innerText = round(datas["avgAge"])
    document.getElementById("most-frequent-purchase-category").innerText = datas["preferedCategory"]
    document.getElementById("sum-of-purchase").innerText = round(datas["sumOfPurchase"])
    document.getElementById("avg-order-value").innerText = round(datas["avgOrderValue"])
    document.getElementById("purchase-frequency").innerText = round(datas["frequency"])
}
function round(num){
    return Math.round(num*100)/100
}
function loadCharts(){
    const genderData = google.visualization.arrayToDataTable(datas["genderDist"])
    const membershipData = google.visualization.arrayToDataTable(datas["membershipDist"])
    const categoriesData = google.visualization.arrayToDataTable(datas["categories"])
    const trendsData = google.visualization.arrayToDataTable(datas["trends"])

    const genderChart = new google.visualization.PieChart(document.getElementById('gender-dist'))
    const membershipChart = new google.visualization.PieChart(document.getElementById('membership-dist'))
    const categoriesChart = new google.visualization.BarChart(document.getElementById('categories'))
    const trendsChart = new google.visualization.LineChart(document.getElementById("trends"))
    
    var containerWidth = document.getElementsByClassName('chartContainer')[0].offsetWidth * 0.9
    var containerHeight = document.getElementsByClassName('chartContainer')[0].offsetHeight * 0.9

    const options = {
        width: containerWidth,
        height: containerHeight,
        legend: { position: 'bottom' }
    }

    const trendsHeight = document.getElementById("trendsChartContainer").offsetHeight * 0.85
    const trendsWidth = document.getElementById("trendsChartContainer").offsetWidth * 0.9
    
    const trendsOptions = {
        width: trendsWidth,
        height: trendsHeight,
        legend: options.legend,
        hAxis: {
            format: ''
        },
    }

    genderChart.draw(genderData,  options)
    membershipChart.draw(membershipData, options)
    categoriesChart.draw(categoriesData, options)
    trendsChart.draw(trendsData, trendsOptions)
}

function loadTable(){
    const table = document.createElement("table")
    const dataSet = datas["topSpenders"]
    
    for(const dataList of dataSet){
        const line = document.createElement("tr")
        for(const element of dataList){
            const td = document.createElement("td")
            if(typeof(element) === "number"){
                td.innerText = round(element)
            }
            else{
                td.innerText = element
            }
            
            line.appendChild(td)
        }
        table.appendChild(line)
    }

    document.getElementById("top-spenders").appendChild(table)
}

