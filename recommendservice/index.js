const express = require("express");
const axios = require("axios");
const _ = require("lodash");
const app = express();
const dotenv = require("dotenv");
const { setupCache } = require('axios-cache-adapter');
dotenv.config();

const port = process.env.PORT || 8093;
const urlProductService = process.env.URL_PRODUCTSERVICE || "http://localhost:8092";

//axios instance with cache
const cache = setupCache({
	maxAge: 2 * 60 * 1000 // 2 min
})

const axiosWithCache = axios.create({
	adapter: cache.adapter
})

// axios configurations for measure request time
axiosWithCache.interceptors.request.use( x => {
	x.meta = x.meta || {}
	x.meta.requestStartedAt = new Date().getTime()
	return x
})

axiosWithCache.interceptors.response.use(x => {
	console.log(`Execution time for: ${x.config.url} - ${new Date().getTime() - x.config.meta.requestStartedAt} ms`)
    return x
})

app.get("/api/recommends", async (req, res) => {
  console.log("Recommendation List");
  try {
    const response = await axiosWithCache.get(urlProductService + "/api/products");
    const responseData = JSON.stringify({
      recommendations: _.sampleSize(response.data.products, 4),
    });
    console.log(responseData);
    res.send(responseData);
  } catch (error) {
    console.log(error);
  }
});

app.listen(port, function () {
  console.log("Product Catalog service has started on port " + port);
});
