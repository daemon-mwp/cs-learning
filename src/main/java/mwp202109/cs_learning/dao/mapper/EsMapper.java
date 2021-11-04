package mwp202109.cs_learning.dao.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class EsMapper {
    private final RestHighLevelClient client;

    public void insertUser(UserDO user) throws IOException {
        IndexRequest request = new IndexRequest();
        request.index("user").id();
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        request.source(userJson, XContentType.JSON);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        log.info("Result===>{}", indexResponse.getResult());
    }

    public void bulkInsertUser(List<UserDO> users) throws IOException {
        BulkRequest request = new BulkRequest();
        ObjectMapper mapper = new ObjectMapper();

        for (UserDO user : users) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index("user").id(user.getId().toString());
            request.add(indexRequest.source(mapper.writeValueAsString(user), XContentType.JSON));
        }

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        log.info("Took===>{}", response.getTook());
        log.info("Items===>{}", response.getItems());
    }

    public void bulkUpdateUser(List<UserDO> users) throws IOException {
        BulkRequest request = new BulkRequest();
        ObjectMapper mapper = new ObjectMapper();
        for (UserDO user : users) {
            UpdateRequest updateRequest = new UpdateRequest("user", user.getId().toString()).doc(mapper.writeValueAsString(user), XContentType.JSON);
            request.add(updateRequest);
        }
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);

        log.info("Took===>{}", response.getTook());
        log.info("Items===>{}", response.getItems());
    }

    public void bulkDeleteUser(List<String> ids) throws IOException {
        BulkRequest request = new BulkRequest();

        ids.forEach(id -> request.add(new DeleteRequest().index("user").id(id)));

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        log.info("Took===>{}", response.getTook());
        log.info("Items===>{}", response.getItems());
    }
}
