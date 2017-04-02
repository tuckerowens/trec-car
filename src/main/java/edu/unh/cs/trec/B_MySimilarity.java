package edu.unh.cs.trec;



import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

public class B_MySimilarity extends Similarity {
    private Similarity sim = null;
    public B_MySimilarity(Similarity sim) {
      this.sim = sim;
    }
    @Override
    public long computeNorm(FieldInvertState state) {
      return sim.computeNorm(state);
    }
    @Override
    public Similarity.SimWeight computeWeight(float queryBoost,CollectionStatistics collectionStats,TermStatistics... termStats) {
      return sim.computeWeight(queryBoost, collectionStats, termStats);
      }
      @Override
      public Similarity.SimScorer simScorer(Similarity.SimWeight weight, AtomicReaderContext context)
      throws IOException {
        final Similarity.SimScorer scorer = sim.simScorer(weight, context);
        final NumericDocValues values = context.reader().getNumericDocValues("ranking");
        return new SimScorer() {
          @Override
          public float score(int i, float v) {
            return values.get(i) * scorer.score(i, v);
          }
          @Override
          public float computeSlopFactor(int i) {
            return scorer.computeSlopFactor(i);
          }
          @Override
          public float computePayloadFactor(int i, int i1, int i2, BytesRef bytesRef) {
          return scorer.computePayloadFactor(i, i1, i2, bytesRef);
          }
        };
      }
  }
