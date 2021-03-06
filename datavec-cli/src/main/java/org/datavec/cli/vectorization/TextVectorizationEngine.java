/*
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.cli.vectorization;

import java.io.IOException;
import java.util.Collection;

import org.datavec.api.writable.Writable;
import org.datavec.cli.shuffle.Shuffler;
import org.datavec.cli.transforms.text.nlp.TfidfTextVectorizerTransform;

public class TextVectorizationEngine extends VectorizationEngine {

  /**
   * Currently the stock input format / RR gives us a vector already converted
   * -	TODO: separate this into a transform plugin
   * <p/>
   * <p/>
   * Thoughts
   * -	Inside the vectorization engine is a great place to put a pluggable transformation system [ TODO: v2 ]
   * -	example: MNIST binarization could be a pluggable transform
   * -	example: custom thresholding on blocks of pixels
   * <p/>
   * <p/>
   * Text Pipeline specific stuff
   * -	so right now the TF-IDF stuff has 2 major issues
   * 1.	its not parallelizable in its current form (loading words into memory doesnt scale)
   * 2.	vectorization is embedded in the inputformat/recordreader - which is conflating functionality
   */
  @Override
  public void execute() throws IOException {


    //	System.out.println( "TextVectorizationEngine > execute() [ START ]" );

    TfidfTextVectorizerTransform tfidfTransform = new TfidfTextVectorizerTransform();
    conf.setInt(TfidfTextVectorizerTransform.MIN_WORD_FREQUENCY, 1);
    //	conf.set(TfidfTextVectorizerTransform.TOKENIZER, "org.datavec.nlp.tokenization.tokenizerfactory.PosUimaTokenizerFactory");
    tfidfTransform.initialize(conf);

    int recordsSeen = 0;


    // 1. collect stats for normalize
    while (reader.hasNext()) {

      // get the record from the input format
      Collection<Writable> w = reader.next();
      tfidfTransform.collectStatistics(w);
      recordsSeen++;

    }

    if (this.printStats) {

      System.out.println("Total Records: " + recordsSeen);
      System.out.println("Total Labels: " + tfidfTransform.getNumberOfLabelsSeen());
      System.out.println("Vocabulary Size of Corpus: " + tfidfTransform.getVocabularySize());
      tfidfTransform.debugPrintVocabList();

    }

    // 2. reset reader

    reader.close();
    //RecordReader reader = null;
    try {
      this.reader = inputFormat.createReader(split, conf);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // 3. transform data

    if (shuffleOn) {

      Shuffler shuffle = new Shuffler();


      // collect the writables into the shuffler

      while (reader.hasNext()) {

        // get the record from the input format
        Collection<Writable> w = reader.next();
        tfidfTransform.transform(w);

        shuffle.addRecord(w);

      }


      // now send the shuffled data out

      while (shuffle.hasNext()) {

        Collection<Writable> shuffledRecord = shuffle.next();
        writer.write(shuffledRecord);

      }

      reader.close();
      writer.close();


    } else {

      while (reader.hasNext()) {

        // get the record from the input format
        Collection<Writable> w = reader.next();
        tfidfTransform.transform(w);

        // the reader did the work for us here
        writer.write(w);

      }


      reader.close();
      writer.close();

    }


  }


}
