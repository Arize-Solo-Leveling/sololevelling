package com.sololevelling.gym.sololevelling.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sololevelling.gym.sololevelling.model.*;
import com.sololevelling.gym.sololevelling.model.dto.admin.RoleUpdateRequest;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonRequest;
import com.sololevelling.gym.sololevelling.model.dto.quest.CreateQuestRequest;
import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutMapper;
import com.sololevelling.gym.sololevelling.service.*;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(AdminControllerTest.MockServiceConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"ADMIN"})
class AdminControllerTest {

    @TestConfiguration
    static class MockServiceConfig {

        @Bean
        public AdminService adminService() {
            return mock(AdminService.class);
        }

        @Bean
        public DungeonService dungeonService() {
            return mock(DungeonService.class);
        }

        @Bean
        public WeeklyDungeonService weeklyDungeonService() {
            return mock(WeeklyDungeonService.class);
        }

        @Bean
        public QuestService questService() {
            return mock(QuestService.class);
        }

        @Bean
        public QuestGenerator questGenerator() {
            return mock(QuestGenerator.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Autowired
    private DungeonService dungeonService;

    @Autowired
    private WeeklyDungeonService weeklyDungeonService;

    @Autowired
    private QuestService questService;

    @Autowired
    private QuestGenerator questGenerator;



    private UUID userId;
    private User mockUser;
    private Dungeon mockDungeon;
    private Quest mockQuest;
    private Workout mockWorkout;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        // Mock User
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setLevel(5);
            Stats stats = new Stats();
            stats.setEndurance(2);
            stats.setStrength(10);
            stats.setAgility(10);
            stats.setIntelligence(5);
            stats.setLuck(5);
        mockUser.setStats(stats);
        mockUser.setExperience(1000);
        mockUser.setUserClass(UserClass.ATHLETE);

        // Mock Dungeon
        mockDungeon = new Dungeon();
        mockDungeon.setId(1L);
        mockDungeon.setName("Mock Dungeon");
        mockDungeon.setType("endurance");
        mockDungeon.setObjective("Survive for 10 mins");
        mockDungeon.setExpReward(150);
        mockDungeon.setLootReward("Epic Loot");
        mockDungeon.setWeekly(false);
        mockDungeon.setUser(mockUser);
        mockDungeon.setCompleted(false);
        mockDungeon.setCreatedAt(LocalDateTime.now());
        mockDungeon.setExpiresAt(LocalDateTime.now().plusDays(7));

        // Mock Quest
        mockQuest = new Quest();
        mockQuest.setId(UUID.randomUUID());
        mockQuest.setTitle("Mock Quest");
        mockQuest.setDescription("Defeat 10 enemies");
        mockQuest.setExperienceReward(200);
        mockQuest.setDaily(true);
        mockQuest.setCreatedAt(LocalDateTime.now());
        mockQuest.setExpiresAt(LocalDateTime.now().plusDays(1));
        mockQuest.setCompleted(false);

        // Mock Workout
        mockWorkout = new Workout();
        mockWorkout.setId(UUID.randomUUID());
        mockWorkout.setName("Push Day");
        mockWorkout.setDate(LocalDateTime.now());
        mockWorkout.setDurationMinutes(60);
        mockWorkout.setTotalVolume(10000);
        mockWorkout.setExperienceGained(300);
        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Bench Press");
        exercise.setReps(10);
        exercise.setSets(3);
        exercise.setWeight(50.0);
        mockWorkout.setExercises(List.of(exercise));

        mockUser.setWorkouts(List.of(mockWorkout));
        mockUser.setQuests(List.of(mockQuest));
    }


    @Test
    void testGetStats() throws Exception {
        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(adminService.getAllUsers()).thenReturn(List.of(mockUser));
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/admin/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserRole() throws Exception {
        RoleUpdateRequest req = new RoleUpdateRequest();
        req.setRole("ADMIN");
        mockMvc.perform(put("/admin/user/" + userId + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(adminService).deleteUser(userId);
        mockMvc.perform(delete("/admin/user/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRoles() throws Exception {
        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDungeons() throws Exception {
        when(dungeonService.getAllDungeons()).thenReturn(List.of(mockDungeon));
        mockMvc.perform(get("/admin/dungeon"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDungeonById() throws Exception {
        when(dungeonService.getDungeonById(1L)).thenReturn(mockDungeon);
        mockMvc.perform(get("/admin/dungeon/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateDungeon() throws Exception {
        DungeonRequest req =new DungeonRequest();
        req.setName("abc");
        req.setType("abc");
        req.setObjective("abc");
        req.setExpReward(10);
        req.setLootReward("hand");
        req.setWeekly(false);
        mockMvc.perform(post("/admin/dungeon/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateRandomDungeon() throws Exception {
        when(weeklyDungeonService.pickRandomWeeklyDungeon(userId)).thenReturn(List.of(mockDungeon));
        mockMvc.perform(post("/admin/dungeon/" + userId + "/generate"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDungeon() throws Exception {
        doNothing().when(dungeonService).deleteDungeonById(1L);
        mockMvc.perform(delete("/admin/dungeon/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllQuests() throws Exception {
        when(questService.getAllQuests()).thenReturn(List.of(mockQuest));
        mockMvc.perform(get("/admin/quests"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetQuestById() throws Exception {
        mockMvc.perform(get("/admin/quests/" + mockQuest.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateQuest() throws Exception {
        CreateQuestRequest req = new CreateQuestRequest();
        req.title = ("Test Quest");
        req.description= ("Defeat 10 goblins");
        req.experienceReward =(100);
        req.daily= (true);
        mockMvc.perform(post("/admin/quests/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateRandomQuests() throws Exception {
        when(questGenerator.pickRandomDailyQuests(userId)).thenReturn(List.of(mockQuest));
        mockMvc.perform(post("/admin/quests/" + userId + "/generate"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteQuest() throws Exception {
        doNothing().when(questService).deleteQuest(userId);
        mockMvc.perform(delete("/admin/quests/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllWorkouts() throws Exception {
        when(adminService.getAllWorkouts()).thenReturn(List.of(WorkoutMapper.toDto(mockWorkout)));
        mockMvc.perform(get("/admin/workout"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWorkoutDetail() throws Exception, AccessDeniedException {
        when(adminService.getWorkoutDetail(mockWorkout.getId())).thenReturn(WorkoutMapper.toDto(mockWorkout));
        mockMvc.perform(get("/admin/workout/" + userId))
                .andExpect(status().isOk());
    }
}
