package one.vladimir.impl.services.rest;

import one.vladimir.api.*;
import one.vladimir.api.pojo.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import one.vladimir.impl.services.point.PointServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeType.OBJECT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
public class RestService {

    @Autowired
    @Qualifier("pointService")
    private PointService pointService;

    @Autowired
    @Qualifier("filterService")
    private FilterService filterService;

    @Autowired
    @Qualifier("routeService")
    private RouteService routeService;

    @Autowired
    @Qualifier("transportService")
    private TransportService transportService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @PostConstruct
    public void postConstructLog(){
        System.out.println("restService initialized");
    }

    @RequestMapping(method = POST, value = "/point/")
    @ResponseBody
    public ResponseEntity<String> addPoint(

            @RequestParam(name = "type", defaultValue = "not given") String type,
            @RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode array = mapper.readValue(configJSON, JsonNode.class);

            if (array.getNodeType() == OBJECT) {

                switch (type) {

                    case "dump":

                        Dump dump = mapper.readValue(configJSON, Dump.class);
                        answerJSON = pointService.addDump(dump);
                        status = CREATED;
                        break;

                    case "base":

                        Base base = mapper.readValue(configJSON, Base.class);
                        answerJSON = pointService.addBase(base);
                        status = CREATED;
                        break;

                    default:
                        answerJSON = "Error. Unsupported type: type =" + type;
                        break;
                }

            }

        } catch (com.fasterxml.jackson.core.JsonParseException e) {

            answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
            e.printStackTrace();

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

            answerJSON = "JSON init exception." +
                    "JSON body contains values which don't exist in type=" + type + " class.";
            e.printStackTrace();

        } catch (java.io.IOException e) {

            System.out.println("Java IO Exception");
            e.printStackTrace();
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/point/{strId}")
    @ResponseBody
    public ResponseEntity<String> getPoint(
            @PathVariable String strId,
            @RequestParam(name = "type", defaultValue = "") String type) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if ((strId.isEmpty()) || (type.equals(""))) {
            answerJSON = "Error. ID or type point aren't given";
        } else {

            try {
                Integer id = Integer.parseInt(strId);

                switch (type) {

                    case "dump":
                        Dump dump = pointService.getDump(id);

                        if (dump != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dump);
                            status = OK;
                        } else {
                            answerJSON = "Dump with id=" + strId + "  not found";
                            status = NOT_FOUND;
                        }

                        break;

                    case "base":

                        Base base = pointService.getBase(id);

                        if (base != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(base);
                            status = OK;
                        } else {
                            answerJSON = "Base with id=" + strId + "  not found";
                            status = NOT_FOUND;
                        }

                        break;

                    default:

                        answerJSON = "Error. Unsupported type: type=" + type;
                        break;
                }

            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a dumb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = POST, value = "/route/")
    @ResponseBody
    public ResponseEntity<String> addRoute(@RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {

            ObjectMapper mapper = new ObjectMapper();
            Route route = mapper.readValue(configJSON, Route.class);
            answerJSON = routeService.addRoute(route);
            status = CREATED;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {

            answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
            e.printStackTrace();

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

            answerJSON = "JSON init exception." +
                    "JSON body contains values which don't exist in route class.";
            e.printStackTrace();

        } catch (java.io.IOException e) {

            System.out.println("Java IO Exception");
            e.printStackTrace();
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/route/{strId}")
    @ResponseBody
    public ResponseEntity<String> getRoute(@PathVariable String strId) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (strId.isEmpty()) {
            answerJSON = "Error. ID isn't given";
        } else {

            try {

                Integer id = Integer.parseInt(strId);
                Route route = routeService.getRoute(id);

                if (route != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(route);
                    status = OK;
                } else {
                    answerJSON = "Route with id=" + strId + "  not found";
                    status = NOT_FOUND;
                }
            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }


    @RequestMapping(method = POST, value = "/vessel/")
    @ResponseBody
    public ResponseEntity<String> addVessel(@RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {

            ObjectMapper mapper = new ObjectMapper();
            Vessel vessel = mapper.readValue(configJSON, Vessel.class);
            answerJSON = transportService.addVessel(vessel);
            status = CREATED;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {

            answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
            e.printStackTrace();

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

            answerJSON = "JSON init exception." +
                    "JSON body contains values which don't exist in vessel class.";
            e.printStackTrace();

        } catch (java.io.IOException e) {

            System.out.println("Java IO Exception");
            e.printStackTrace();
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/vessel/{strId}")
    @ResponseBody
    public ResponseEntity<String> getVessel(@PathVariable String strId) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (strId.isEmpty()) {
            answerJSON = "Error. ID isn't given";
        } else {

            try {

                Integer id = Integer.parseInt(strId);
                Vessel vessel = transportService.getVessel(id);

                if (vessel != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vessel);
                    status = OK;
                } else {
                    answerJSON = "Vessel with id=" + strId + "  not found";
                    status = NOT_FOUND;
                }
            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }




    /* Uncomment and fix in the future when Update modules comes and birds sings with a beautiful voices.
    @RequestMapping(method = PUT, value = "/point/{strId}")
    @ResponseBody

    public ResponseEntity<String> updatePoint(
            @PathVariable String strId,
            @RequestParam(name = "type", defaultValue = "") String type,
            @RequestBody String configJSON){

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if((strId.isEmpty()) || (type.equals(""))) {
            answerJSON = "Error. ID or type point aren't given";
        }

        else {

            try {
                Integer id = Integer.parseInt(strId);

                try {

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode array = mapper.readValue(configJSON, JsonNode.class);

                    if (array.getNodeType() == OBJECT) {

                        switch (type) {

                            case "dump":

                                Dump dump = mapper.readValue(configJSON, Dump.class);
                                answerJSON = "UPD dump";//pointService.addDump(dump);
                                status = CREATED;
                                break;

                            case "base":

                                Base base = mapper.readValue(configJSON, Base.class);
                                answerJSON = "UPD base";//pointService.addBase(base);
                                status = CREATED;
                                break;

                            default:
                                answerJSON = "Error. Unsupported type: type =" + type;
                                break;
                        }

                    }

                } catch (com.fasterxml.jackson.core.JsonParseException e) {

                    answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
                    e.printStackTrace();

                } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

                    answerJSON = "JSON init exception." +
                            "JSON body contains values which don't exist in type=" + type +" class.";
                    e.printStackTrace();

                } catch (java.io.IOException e) {

                    System.out.println("Java IO Exception");
                    e.printStackTrace();
                }


            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }
    */


    // Test methods

    @RequestMapping(method = POST, value = "/user/")
    @ResponseBody
    public ResponseEntity<String> addUser(@RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(configJSON, User.class);
            answerJSON = userService.addUser(user);
            status = CREATED;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {

            answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
            e.printStackTrace();

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

            answerJSON = "JSON init exception." +
                    "JSON body contains values which don't exist in vessel class.";
            e.printStackTrace();

        } catch (java.io.IOException e) {

            System.out.println("Java IO Exception");
            e.printStackTrace();
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/user/{strId}")
    @ResponseBody
    public ResponseEntity<String> getUser(@PathVariable String strId) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (strId.isEmpty()) {
            answerJSON = "Error. ID isn't given";
        } else {

            try {

                Integer id = Integer.parseInt(strId);
                User user = userService.getUser(id);

                if (user != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
                    status = OK;
                } else {
                    answerJSON = "User with id=" + strId + "  not found";
                    status = NOT_FOUND;
                }
            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = POST, value = "/group/")
    @ResponseBody
    public ResponseEntity<String> addGroup(@RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {

            ObjectMapper mapper = new ObjectMapper();
            Group group = mapper.readValue(configJSON, Group.class);
            answerJSON = pointService.addGroup(group);
            status = CREATED;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {

            answerJSON = "JSON parsing exception: can't read JSON structure because there are some mistakes!";
            e.printStackTrace();

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {

            answerJSON = "JSON init exception." +
                    "JSON body contains values which don't exist in vessel class.";
            e.printStackTrace();

        } catch (java.io.IOException e) {

            System.out.println("Java IO Exception");
            e.printStackTrace();
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/group/{strId}")
    @ResponseBody
    public ResponseEntity<String> getGroup(@PathVariable String strId) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (strId.isEmpty()) {
            answerJSON = "Error. ID isn't given";
        } else {

            try {

                Integer id = Integer.parseInt(strId);
                Group group = pointService.getGroup(id);

                if (group != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(group);
                    status = OK;
                } else {
                    answerJSON = "Group with id=" + strId + "  not found";
                    status = NOT_FOUND;
                }
            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    @RequestMapping(method = GET, value = "/create_route/{strId}")
    @ResponseBody
    public ResponseEntity<String> testRoute(@PathVariable String strId) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (strId.isEmpty()) {
            answerJSON = "Error. ID isn't given";
        } else {

            try {

                Integer id = Integer.parseInt(strId);
                Route route = routeService.createRoute(id);

                if (route != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(route);
                    status = OK;
                } else {
                    answerJSON = "Group with id=" + strId + "  not found";
                    status = NOT_FOUND;
                }
            } catch (NumberFormatException e) {

                answerJSON = "Error. Given ID doesn't a numb";
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                answerJSON = "Can't parse class to JSON";
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(answerJSON, status);
    }

    /*@RequestMapping(method = GET, value = "/add_user/")
    public ResponseEntity<String> addUser() {

        String result = UserService.addUser();
        return new ResponseEntity<>(result, OK);
    }

    @RequestMapping(method = GET, value = "/get_user/")
    public ResponseEntity<String> getUser() {


        // It works but you should chose the correct ID
        User user = userService.getUser(1);
        return new ResponseEntity<>(user, OK);
    }

    @RequestMapping(method = GET, value = "/test_geo")
    public ResponseEntity<String> testGeo(
            @RequestBody String data) {


        return new ResponseEntity(pointService.testGeo(data), OK);
    }*/

    @RequestMapping(method = GET, value = "/test_geo")
    public ResponseEntity<String> testGeo(
            @RequestParam(name = "command", defaultValue = "")   String command) {

        return new ResponseEntity(pointService.testGeo(command), OK);
    }


    @RequestMapping(method = GET, value = "/points/")
    @ResponseBody
    public ResponseEntity<String> getPoints(

            @RequestParam(name = "type", defaultValue = "not given") String type,
            @RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;
//        System.out.println(configJSON);
        DumpFilter dumpFilter = filterService.createDumpFilterFromJson(configJSON);
        List<Dump> dumps = pointService.getDumpsByFilter(dumpFilter);
        try {
            ObjectMapper mapper = new ObjectMapper();
            answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dumps);
            status = HttpStatus.OK;
        } catch (JsonProcessingException e) {
            //TODO: implement exception handling
        }
        return new ResponseEntity<>(answerJSON, status);
    }


    @RequestMapping(method = GET, value = "/routes/")
    @ResponseBody
    public ResponseEntity<String> getRoutes(
            @RequestBody String configJSON) {

        String answerJSON = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        System.out.println(configJSON);
        RouteFilter routeFilter = filterService.createRouteFilterFromJson(configJSON);
        List<Route> routes = routeService.getRoutesByFilter(routeFilter);
        try {
            ObjectMapper mapper = new ObjectMapper();
            answerJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(routes);
            status = HttpStatus.OK;
        } catch (JsonProcessingException e) {
            //TODO: implement exception handling
        }
        return new ResponseEntity<>(answerJSON, status);
    }


}