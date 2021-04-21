const express = require("express");
const axios = require("axios");
const _ = require("lodash");
const app = express();
const dotenv = require("dotenv");
dotenv.config();

const port = process.env.PORT || 8093;
const urlProductService =
  process.env.URL_PRODUCTSERVICE || "http://localhost:8092";

app.get("/api/recommends", async (req, res) => {
  console.log("Recommendation List");
  try {
    const response = await axios.get(urlProductService + "/api/products");
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
