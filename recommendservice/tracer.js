'use strict';

const api = require('@opentelemetry/api');
const { NodeTracerProvider } = require('@opentelemetry/node');
const { registerInstrumentations } = require('@opentelemetry/instrumentation');
const { SimpleSpanProcessor } = require('@opentelemetry/tracing');
const { JaegerExporter } = require('@opentelemetry/exporter-jaeger');
const { B3Propagator } = require('@opentelemetry/propagator-b3');
const { AlwaysOnSampler } = require("@opentelemetry/core");
const { HttpInstrumentation } = require('@opentelemetry/instrumentation-http');

module.exports = (serviceName) => {
    const provider = new NodeTracerProvider({
        sampler: new AlwaysOnSampler()
    });

    const exporter = new JaegerExporter({
        serviceName: serviceName,
        endpoint: process.env.OTEL_EXPORTER_JAEGER_ENDPOINT,
    });

    provider.addSpanProcessor(new SimpleSpanProcessor(exporter));
    provider.register();

    api.propagation.setGlobalPropagator(new B3Propagator());

    registerInstrumentations({
        instrumentations: [
            new HttpInstrumentation(),
        ],
        traceProvider: provider,
    });
    return api.trace.getTracer(serviceName);

};