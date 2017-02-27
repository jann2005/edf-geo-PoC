
Description:
This POC implemented to integrate data from geo cloud to Predix cloud. EDF smart meter sends energy consumption (electricity and gas usage) to geo cloud. EDF customer views periodoc electricity and gas usage
data on the UI dashboard. Consumer can also see live usage of his energy consumption. 


Main Components
  SQS Event Subscriber
  Backend API
  UI
  
Predix services used in the POC

  Predix UAA
  Predix Timeseries
  Predix UI

High level Design:

Smart meter publishes energy consumption encoded data to AWS SQS service. Two types of energy data are published in geo cloud queues- live and periodic
Subscribers in Predix cloud
      -live data subscriber
      -Periodic data subscriber
 
Periodic subscriber reads data from SQS queue and ingest into timeseries instance. Live data subscriber reads data from SQS queue and send to web socket. UI listens to websocket and display live data
