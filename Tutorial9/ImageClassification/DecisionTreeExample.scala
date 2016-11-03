package modelBuilder

import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Naga on 13-10-2016.
  * modified by Justin Baraboo on 31-10-2016
  */
object DecisionTreeExample {
  def main(args: Array[String]) {
    System.setProperty("hadoop.hom.dir", "C:\\Users\\Justin Baraboo\\Desktop\\winutils");
    val sparkConf = new SparkConf().setAppName("DecisionTreeExample").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    System.out.println("cats");



    val data = MLUtils.loadLibSVMFile(sc, "data/Features.txt")
    System.out.println("Dogs");

    // Split the data into training and test sets (30% held out for testing)
    val splits = data.randomSplit(Array(0.7, 0.3))
    val (trainingData, testData) = (splits(0), splits(1))

    // Train a DecisionTree model.
    //  Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 4
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val maxDepth = 30
    val maxBins = 32

    val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      impurity, maxDepth, maxBins)

    // Evaluate model on test instances and compute test error
    val labelAndPreds = testData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val testErr = labelAndPreds.filter(r => r._1 != r._2).count().toDouble / testData.count()
    println("Test Error = " + testErr)
    println("Learned classification tree model:\n" + model.toDebugString)

    // Save and load model
//    model.save(sc, "data/myDecisionTreeClassificationModel")
    System.out.println(model.toString())
    println("Test Error = " + testErr)

    MongoCall.insertIntoMongoDB(model.toDebugString);
//    val sameModel = DecisionTreeModel.load(sc, "target/tmp/myDecisionTreeClassificationModel")
  }
}
