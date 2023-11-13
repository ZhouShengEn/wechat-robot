package com.zhoushengen.robot.component;

import com.zhoushengen.robot.wechat.dto.openai.OpenAIEmbeddingResDTO;
import com.zhoushengen.robot.wechat.remote.OpenAIRemoteApi;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/6 17:52
 **/
@Component
@Slf4j
public class MilvusService {


    @Value("${milvus.host}")
    private String milvusHost;

    @Value("${milvus.port}")
    private Integer milvusPort;

    @Value("${milvus.collection}")
    private String milvusCollection;

    @Value("${milvus.outputField}")
    private String milvusOutputField;

    @Autowired
    private OpenAIRemoteApi openAIRemoteApi;


    /**
     * 向量搜索
     * @param wxidTo
     * @param prompt
     * @return
     */
    public String embeddingSearch(String wxidTo, String prompt) {
        MilvusServiceClient milvusClient = new MilvusServiceClient(
            ConnectParam.newBuilder()
                .withHost(milvusHost)
                .withPort(milvusPort)
                .build()
        );
        milvusClient.loadCollection(
            LoadCollectionParam.newBuilder()
                .withCollectionName(milvusCollection)
                .build()
        );
        final Integer SEARCH_K = 14;                       // TopK
        List<String> search_output_fields = Arrays.asList(milvusOutputField);
        log.info("1.wechat embeddingSearch prompt:{}, wxidTo:{}", prompt, wxidTo);
        OpenAIEmbeddingResDTO openAIEmbeddingResDTO = openAIRemoteApi.requestEmbedding(prompt);
        List<List<Float>> search_vectors = Arrays.asList(openAIEmbeddingResDTO.getData().get(0).getEmbedding());
        String SEARCH_PARAM = "{\"nprobe\":100}";
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(milvusCollection)
            .withMetricType(MetricType.L2)
            .withOutFields(search_output_fields)
            .withTopK(SEARCH_K)
            .withVectors(search_vectors)
            .withVectorFieldName("embedding")
            .withParams(SEARCH_PARAM)
            .build();
        R<SearchResults> respSearch = milvusClient.search(searchParam);
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
        List<String> titles = (List<String>) wrapperSearch.getFieldData(milvusOutputField, 0);
        StringBuilder promptFinal = new StringBuilder(
            "回答时只能基于以下已知信息，简洁和专业地回答用户问题，不允许扩展已知信息以外的内容。\\n已知内容:\\n");
        Integer count;
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(titles)) {
            for (int i = 0; i < titles.size(); i++) {
                String title = titles.get(i);
                count = i + 1;
                promptFinal.append(count).append(". ").append(title).append("\\n");
            }
            promptFinal.append("问题:").append(prompt);
            log.info("2.wechat embeddingSearch success wxidTo:{}, promptFinal:{}", wxidTo, promptFinal.toString());

            return promptFinal.toString();
        }

        return "抱歉，未查询到相关信息";
    }
}
